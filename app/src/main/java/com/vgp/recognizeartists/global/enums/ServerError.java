package com.vgp.recognizeartists.global.enums;

public enum ServerError {
    Base64Error,
    ServerMaintance,
    ArtistNotRecognized,
    UnknowError;

    public static String convertServerErrorToMsg(ServerError serverError) {
        switch (serverError){
            case Base64Error:
                return "Error while send image to server, check your internet connection and try again";
            case ServerMaintance:
                return "Server is in maintance, Try Again";
            case ArtistNotRecognized:
                return "Artist not Recognized, Try Again";
            case UnknowError:
                return "Unknow Error, Try Again";
            default:
                return "Unknow Error, Try Again";
        }
    }
}


