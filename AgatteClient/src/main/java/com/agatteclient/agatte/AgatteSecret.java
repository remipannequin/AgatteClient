/*
 * This file is part of AgatteClient.
 *
 * AgatteClient is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AgatteClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AgatteClient.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2015 Rémi Pannequin (remi.pannequin@gmail.com).
 */

package com.agatteclient.agatte;

/**
 * Place holder to get the "secrets"
 *
 * Created by Rémi Pannequin on 01/07/15.
 */
public class AgatteSecret {

    private String header_key;
    private String url;
    private String secret;

    public AgatteSecret(String url, String header_key, String secret) {
        this.header_key = header_key.trim();
        this.url = url.trim();
        this.secret = secret.trim();
    }

    public String getHeader_key() {
        return header_key;
    }

    public String getUrl() {
        return url;
    }

    public String getSecret() {
        return secret;
    }

    public String getURLEncodedSecret() {
        return secret.replaceAll("=", "%3D");
    }
}
