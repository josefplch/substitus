package com.github.josefplch.utils.system;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Serialize & deserialize any object.
 * 
 * @author  Josef Plch
 * @since   2019-04-02
 * @version 2019-11-14
 */
public abstract class BinarySerializer {
    public static byte [] serialize (Object object) throws IOException {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream ()) {
            try (ObjectOutputStream objectStream = new ObjectOutputStream (byteStream)){
                objectStream.writeObject (object);
            }
            return byteStream.toByteArray ();
        }
    }

    public static <T> T deserialize (byte [] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream (bytes)){
            return deserialize (byteStream);
        }
    }
    
    @SuppressWarnings ("unchecked")
    public static <T> T deserialize (InputStream inputStream) throws IOException, ClassNotFoundException {
        try (ObjectInputStream objectStream = new ObjectInputStream (inputStream)){
            return (T) objectStream.readObject ();
        }
    }
    
    public static <T> T deserializeFast (String filePath) throws IOException, ClassNotFoundException {
        return deserialize (Files.readAllBytes (Paths.get (filePath)));
    }
    
    public static <T> T deserializeSlow (String filePath) throws IOException, ClassNotFoundException {
        try (FileInputStream fileStream = new FileInputStream (filePath)){
            return deserialize (fileStream);
        }
    }
}
