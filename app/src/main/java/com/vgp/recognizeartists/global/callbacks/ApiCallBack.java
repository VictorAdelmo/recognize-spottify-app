package com.vgp.recognizeartists.global.callbacks;

import com.vgp.recognizeartists.global.enums.ServerError;
import com.vgp.recognizeartists.modules.model.ArtistModel;

public interface ApiCallBack {
    void success(ArtistModel artistModel);
    void error(ServerError error);
}
