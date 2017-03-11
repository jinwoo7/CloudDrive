package com.mycompany.entityclasses;

import com.mycompany.entityclasses.UserFile;
import com.mycompany.entityclasses.UserPhoto;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-02-24T20:01:37")
@StaticMetamodel(User.class)
public class User_ { 

    public static volatile SingularAttribute<User, String> lastName;
    public static volatile SingularAttribute<User, String> address2;
    public static volatile SingularAttribute<User, String> city;
    public static volatile SingularAttribute<User, Integer> securityQuestion;
    public static volatile SingularAttribute<User, String> securityAnswer;
    public static volatile SingularAttribute<User, String> address1;
    public static volatile CollectionAttribute<User, UserPhoto> userPhotoCollection;
    public static volatile SingularAttribute<User, String> zipcode;
    public static volatile SingularAttribute<User, String> firstName;
    public static volatile SingularAttribute<User, String> password;
    public static volatile CollectionAttribute<User, UserFile> userFileCollection;
    public static volatile SingularAttribute<User, String> middleName;
    public static volatile SingularAttribute<User, Integer> id;
    public static volatile SingularAttribute<User, String> state;
    public static volatile SingularAttribute<User, String> email;
    public static volatile SingularAttribute<User, String> username;

}