/*
 * Created by Jinwoo Yom on 2017.02.21  * 
 * Copyright © 2017 Jinwoo Yom. All rights reserved. * 
 */
package com.mycompany.managers;

import com.mycompany.entityclasses.User;
import com.mycompany.entityclasses.UserFile;
import com.mycompany.sessionbeans.UserFacade;
import com.mycompany.sessionbeans.UserFileFacade;

import com.mycompany.jsfclasses.UserFileController;
import javax.inject.Inject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.model.UploadedFile;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author Balci
 */
@Named(value = "fileUploadManager")
@SessionScoped
/**
 *
 * @author Yom
 */
public class FileUploadManager implements Serializable {

    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private UploadedFile uploadedFile;

    /*
    The instance variable 'userFacade' is annotated with the @EJB annotation.
    The @EJB annotation directs the EJB Container (of the GlassFish AS) to inject (store) the object reference
    of the UserFacade object, after it is instantiated at runtime, into the instance variable 'userFacade'.
     */
    @EJB
    private UserFacade userFacade;

    /*
    The instance variable 'userFileFacade' is annotated with the @EJB annotation.
    The @EJB annotation directs the EJB Container (of the GlassFish AS) to inject (store) the object reference 
    of the UserFileFacade object, after it is instantiated at runtime, into the instance variable 'userFileFacade'.
     */
    @EJB
    private UserFileFacade userFileFacade;

    /*
    The instance variable 'userFileController' is annotated with the @Inject annotation.
    The @Inject annotation directs the JavaServer Faces (JSF) CDI Container to inject (store) the object reference 
    of the UserFileController object, after it is instantiated at runtime, into the instance variable 'userFileController'.
    
    We can do this because we annotated the UserFileController class with @Named to indicate
    that the CDI container will manage the objects instantiated from the UserFileController class.
     */
    @Inject
    private UserFileController userFileController;

    // Resulting FacesMessage produced
    FacesMessage resultMsg;

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    // Returns the uploaded uploadedFile
    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    // Obtains the uploaded uploadedFile
    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public UserFileFacade getUserFileFacade() {
        return userFileFacade;
    }

    public UserFileController getUserFileController() {
        return userFileController;
    }

    /*
    ================
    Instance Methods
    ================
     */
    public void handleFileUpload(FileUploadEvent event) throws IOException {

        try {
            String user_name = (String) FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap().get("username");

            User user = getUserFacade().findByUsername(user_name);

            /*
            To associate the file to the user, record "userId_filename" in the database.
            Since each file has its own primary key (unique id), the user can upload
            multiple files with the same name.
             */
            String userId_filename = user.getId() + "_" + event.getFile().getFileName();

            /*
            "The try-with-resources statement is a try statement that declares one or more resources. 
            A resource is an object that must be closed after the program is finished with it. 
            The try-with-resources statement ensures that each resource is closed at the end of the
            statement." [Oracle] 
             */
            try (InputStream inputStream = event.getFile().getInputstream();) {

                // The method inputStreamToFile given below writes the uploaded file into the CloudStorage/FileStorage directory.
                inputStreamToFile(inputStream, userId_filename);
                inputStream.close();
            }

            /*
            Create a new UserFile object with attibutes: (See UserFile table definition inputStream DB)
                <> id = auto generated as the unique Primary key for the user file object
                <> filename = userId_filename
                <> user_id = user
             */
            UserFile newUserFile = new UserFile(userId_filename, user);

            /*
            ==============================================================
            If the userId_filename was used before, delete the earlier file.
            ==============================================================
             */
            List<UserFile> filesFound = getUserFileFacade().findByFilename(userId_filename);

            /*
            If the userId_filename already exists in the database, 
            the filesFound List will not be empty.
             */
            if (!filesFound.isEmpty()) {

                // Remove the file with the same name from the database
                getUserFileFacade().remove(filesFound.get(0));
            }

            //---------------------------------------------------------------
            //
            // Create the new UserFile entity (row) in the CloudDriveDB
            getUserFileFacade().create(newUserFile);

            // This sets the necessary flag to ensure the messages are preserved.
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

            getUserFileController().refreshFileList();

            resultMsg = new FacesMessage("File(s) Uploaded Successfully!");
            FacesContext.getCurrentInstance().addMessage(null, resultMsg);

            // After successful upload, show the UserFiles.xhtml facelets page
            FacesContext.getCurrentInstance().getExternalContext().redirect("UserFiles.xhtml");

        } catch (IOException e) {
            resultMsg = new FacesMessage("Something went wrong during file upload! See: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, resultMsg);
        }

    }

    // Show the File Upload Page
    public String showFileUploadPage() {

        return "UploadFile?faces-redirect=true";
    }

    public void upload() throws IOException {

        if (getUploadedFile().getSize() != 0) {
            copyFile(getUploadedFile());
        }
    }

    /**
     * cancel an upload
     *
     * @return
     */
    public String cancel() {
        //message = "";
        return "Profile?faces-redirect=true";
    }

    /**
     * Used to copy a uploadedFile
     *
     * @param file
     * @return
     * @throws java.io.IOException
     */
    public FacesMessage copyFile(UploadedFile file) throws IOException {
        try {
            String user_name = (String) FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap().get("username");

            User user = getUserFacade().findByUsername(user_name);

            /*
            To associate the file to the user, record "userId_filename" in the database.
            Since each file has its own primary key (unique id), the user can upload
            multiple files with the same name.
             */
            String userId_filename = user.getId() + "_" + file.getFileName();

            /*
            "The try-with-resources statement is a try statement that declares one or more resources. 
            A resource is an object that must be closed after the program is finished with it. 
            The try-with-resources statement ensures that each resource is closed at the end of the
            statement." [Oracle] 
             */
            try (InputStream inputStream = file.getInputstream();) {

                // The method inputStreamToFile is given below.
                inputStreamToFile(inputStream, userId_filename);
                inputStream.close();
            }

            resultMsg = new FacesMessage("");  // No need to show a result message

        } catch (IOException e) {
            resultMsg = new FacesMessage("Something went wrong during file copy! See:" + e.getMessage());
        }

        // This sets the necessary flag to ensure the messages are preserved.
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

        FacesContext.getCurrentInstance().addMessage(null, resultMsg);
        return resultMsg;
    }

    private File inputStreamToFile(InputStream inputStream, String file_name)
            throws IOException {

        // Read the series of bytes from the input stream
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);

        // Write the series of bytes on uploadedFile.
        File targetFile = new File(Constants.FILES_ABSOLUTE_PATH, file_name);

        OutputStream outStream;
        outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        outStream.close();

        return targetFile;
    }

    /**
     * Sets the file location
     *
     * @param data
     */
    public void setFileLocation(UserFile data) {

        String fileName = data.getFilename();

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getFlash().put("data", data);
    }

    public String getFileLocation() {

        return Constants.FILES_ABSOLUTE_PATH;
    }

    /**
     * Used to return the file extension for a file.
     *
     * @param filename
     * @return
     */
    public static String getExtension(String filename) {

        if (filename == null) {
            return null;
        }
        int extensionPos = filename.lastIndexOf('.');

        int lastUnixPos = filename.lastIndexOf('/');
        int lastWindowsPos = filename.lastIndexOf('\\');
        int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);
        int index = lastSeparator > extensionPos ? -1 : extensionPos;

        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

}
