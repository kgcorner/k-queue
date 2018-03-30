package com.lluvia.security;

import com.kgcorner.lluvia.model.Application;
import com.lluvia.exception.InvalidTokenException;

/**
 * Created by admin on 3/28/2018.
 */
public interface Verifier {
    Application verify(String token) throws InvalidTokenException;
}
