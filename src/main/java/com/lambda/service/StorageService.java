package com.lambda.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import com.lambda.exception.FileNotFoundException;
import com.lambda.exception.FileStorageException;
import com.lambda.model.entity.Album;
import com.lambda.model.entity.Artist;
import com.lambda.model.entity.Song;
import com.lambda.model.entity.User;
import com.lambda.model.util.MediaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;

public abstract class StorageService<T> {
    @Autowired
    ArtistService artistService;

    private void normalizeFileName(String fileName) {
        if (fileName.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
        }
    }

    private String getOldExtension(String url) {
        return url.substring(url.lastIndexOf(".") + 1);
    }

    private String getNewExtension(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        return originalFileName!=null?originalFileName.substring(originalFileName.lastIndexOf(".") + 1):"";
    }

    public String getOldFileName(Object object) {
        if (object instanceof Song) {
            Song song = (Song) object;
            String url = song.getUrl();
            String oldExtension = getOldExtension(url);
            Collection<Artist> artists = song.getArtists();
            String artistsString = artistService.convertToString(artists);
            return song.getId().toString().concat(" - ").concat(song.getTitle()).concat(artistsString).concat(".").concat(oldExtension);
        } else if (object instanceof Album) {
            Album album = (Album) object;
            String url = album.getCoverUrl();
            String oldExtension = getOldExtension(url);
            Collection<Artist> artists = album.getArtists();
            String artistsString = artistService.convertToString(artists);
            return album.getId().toString().concat(" - ").concat(album.getTitle()).concat(artistsString).concat(".").concat(oldExtension);
        } else if (object instanceof User) {
            User user = (User) object;
            String avatarUrl = user.getAvatarUrl();
            String oldExtension = getOldExtension(avatarUrl);
            return user.getId().toString().concat(" - ").concat(user.getUsername()).concat(".").concat(oldExtension);
        } else if (object instanceof Artist) {
            Artist user = (Artist) object;
            String avatarUrl = user.getAvatarUrl();
            String oldExtension = getOldExtension(avatarUrl);
            return user.getId().toString().concat(" - ").concat(user.getName()).concat(".").concat(oldExtension);
        } else return null;

    }

    private String getNewFileName(Object object, MultipartFile file) {
        String extension = getNewExtension(file);
        if (object instanceof MediaObject) {
            Collection<Artist> artists;
            String artistsString;
            if (object instanceof Song) {
                Song song = (Song) object;
                artists = song.getArtists();
                artistsString = artistService.convertToString(artists);
                return StringUtils.cleanPath(song.getId().toString().concat(" - ").concat(song.getTitle()).concat(artistsString).concat(".").concat(extension));
            } else if (object instanceof Album) {
                Album album = (Album) object;
                artists = album.getArtists();
                artistsString = artistService.convertToString(artists);
                return StringUtils.cleanPath(album.getId().toString().concat(" - ").concat(album.getTitle()).concat(artistsString).concat(".").concat(extension));
            } else return null;
        } else if (object instanceof User) {
            User user = (User) object;
            return user.getId().toString().concat(" - ").concat(user.getUsername()).concat(".").concat(extension);
        } else if (object instanceof Artist) {
            Artist artist = (Artist) object;
            return artist.getId().toString().concat(" - ").concat(artist.getName()).concat(".").concat(extension);
        }
        else return null;
    }

//    public void deleteOldFile(Path storageLocation, Object object, MultipartFile file) {
//        String newExtension = getNewExtension(file);
//        // check if new image ext is different from old file ext
//        String url = "";
//        if (object instanceof Song) {
//            url = ((Song) object).getUrl();
//        } else if (object instanceof Album) {
//            url = ((Album) object).getCoverUrl();
//        } else if (object instanceof User) {
//            url = ((User) object).getAvatarUrl();
//        } else if (object instanceof Artist) {
//            url = ((Artist) object).getAvatarUrl();
//        }
//        if (url != null && !url.equals("")) {
//            String oldFileName = getOldFileName(object);
//            String oldExtension = getOldExtension(url);
//            if (!oldExtension.equals(newExtension)) {
//                deleteLocalStorageFile(storageLocation, oldFileName.concat(".").concat(oldExtension));
//            }
//        }
//    }

    public Resource loadFileAsResource(Path storageLocation, String fileName) {
        try {
            Path filePath = storageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName, ex);
        }
    }

    private StorageClient getFirebaseStorage() {
        try {
//            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("/Users/nguyenxuanhoang/Documents/ThucHanhCodeGym/adminsdk/climax-sound-firebase-adminsdk-c29fo-27166cf850.json"))
//                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
//            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("/mnt/D43C7B5B3C7B3816/CodeGym/Module 4/Project Climax Sound/climax-sound-firebase-adminsdk-c29fo-27166cf850.json"))
//                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setDatabaseUrl("https://climax-sound.firebaseio.com")
                    .setStorageBucket("climax-sound.appspot.com")
                    .build();

            FirebaseApp fireApp = null;
            List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
            if(firebaseApps!=null && !firebaseApps.isEmpty()){
                for(FirebaseApp app : firebaseApps){
                    if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
                        fireApp = app;
                }
            }
            else
                fireApp = FirebaseApp.initializeApp(options);
            return StorageClient.getInstance(fireApp);
        } catch (IOException ex) {
            throw new FileStorageException("Could not get admin-sdk json file. Please try again!", ex);
        }
    }

    public String saveToFirebaseStorage(Object object, MultipartFile file) {
        String fileName = getNewFileName(object, file);
        normalizeFileName(fileName);
        StorageClient storageClient = getFirebaseStorage();
        Bucket bucket = storageClient.bucket();
        try {
            InputStream testFile = file.getInputStream();
            String blobString = "";
            if (object instanceof Song) {
                blobString = "audio/" + fileName;
            } else if (object instanceof Album) {
                blobString = "cover/" + fileName;
            } else if (object instanceof User | object instanceof Artist) {
                blobString = "avatar/" + fileName;
            }
            Blob blob = bucket.create(blobString, testFile, Bucket.BlobWriteOption.userProject("climax-sound"));
            bucket.getStorage().updateAcl(blob.getBlobId(), Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
            String blobName = blob.getName();
            if (object instanceof Song) {
                ((Song) object).setBlobId(blobName);
            } else if (object instanceof Album) {
                ((Album) object).setCoverBlobId(blobName);
            } else if (object instanceof User) {
                ((User) object).setAvatarBlobString(blobName);
            } else if (object instanceof Artist) {
                ((Artist) object).setAvatarUrl(blobName);
            }
            return blob.getMediaLink();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

//    public String saveToLocalStorage(Path storageLocation, Object object, MultipartFile file) {
//        String fileName = getNewFileName(object, file);
//        String rootUri = "";
//        if (object instanceof Song) {
//            rootUri = "/api/song/download/";
//        } else if (object instanceof Album) {
//            rootUri = "/api/album/download/";
//        } else if (object instanceof User | object instanceof Artist) {
//            rootUri = "/api/avatar/download/";
//        }
//        normalizeFileName(fileName);
//        try {
//            // Check if the file's title contains invalid characters
//            if (fileName.contains("..")) {
//                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
//            }
//            // Copy file to the target location (Replacing existing file with the same title)
//            Path targetLocation = storageLocation.resolve(fileName);
//            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//            return ServletUriComponentsBuilder.fromCurrentContextPath().path(rootUri).path(fileName).toUriString();
//        } catch (IOException ex) {
//            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
//        }
//    }

    public void deleteFirebaseStorageFile(Object object) {
        StorageClient storageClient = getFirebaseStorage();
        String blobString = "";
        if (object instanceof Song) {
            blobString = ((Song) object).getBlobId();
        } else if (object instanceof Album) {
            blobString = ((Album) object).getCoverBlobId();
        } else if (object instanceof User) {
            blobString = ((User) object).getAvatarBlobString();
        } else if (object instanceof Artist) {
            blobString = ((Artist) object).getAvatarBlobId();
        }
        BlobId blobId = BlobId.of(storageClient.bucket().getName(),blobString);
        storageClient.bucket().getStorage().delete(blobId);
    }


    public Boolean deleteLocalStorageFile(Path storageLocation, String fileName) {
        Path filePath = storageLocation.resolve(fileName).normalize();
        File file = filePath.toFile();
        return file.delete();
    }
}
