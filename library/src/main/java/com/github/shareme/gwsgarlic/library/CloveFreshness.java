/*
Copyright (C) 2015 Fred Grott(aka shareme GrottWorkShop)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under License.
 */
package com.github.shareme.gwsgarlic.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * CloveFreshness
 *
 * in main Activity or Application class:
 * <code>
 *     private static String sMyCertHash = null;
 *     private static String myCertHash(Context context){
 *         if (sMyCertHash == null){
 *             if (BuildConfig.DEBUG){
 *                 Clove.setClove("0EFB7236 328348A9 89718BAD DF57F544 D5CCB4AE B9DB34BC 1E29DD26 F77C8255");
 *             }else{
 *                 Clove.setClove("hash of your production keystore");
 *             }
 *         }
 *         return sMyCertHash
 *     }
 *
 *     onCreate(){
 *
 *         if (!CloveFreshness.test(this, this.getPackageName(), myCertHash(this))){
 *             finish();
 *             return;
 *         }else{
 *             System.exit();
 *         }
 *
 *     }
 * </code>
 *
 * It is optional if you want to indicate to the poor app victim that they downloaded a pirated app,
 * my opinion is that you should show a toast indicating actual name of your app with a user confirmation
 * button as that way they will remember the correct product name and search for it and probably
 * even but it and install it.
 *
 * Created by fgrott on 11/19/2015.
 */
@SuppressWarnings("unused")
public class CloveFreshness {

    public static boolean test(Context ctx, String pkgname, String correctHash){
        if (correctHash == null) return false;
        correctHash = correctHash.replaceAll("","");
        return correctHash.equals(hash(ctx, pkgname));
    }

    public static String hash(Context ctx, String pkgname){
        if (pkgname == null) return null;
        try {
            PackageManager pm = ctx.getPackageManager();
            @SuppressLint("PackageManagerGetSignatures") PackageInfo pkginfo = pm.getPackageInfo(pkgname, PackageManager.GET_SIGNATURES);
            if (pkginfo.signatures.length != 1)
                return null; //does not handle more than one signature
            Signature sig = pkginfo.signatures[0];
            byte[] cert = sig.toByteArray();
            byte[] sha256 = computeSha256(cert);
            return byte2hex(sha256);
        }catch(PackageManager.NameNotFoundException e){
            return  null;
        }
    }

    private static byte[] computeSha256(byte[] data){
        try{
            return MessageDigest.getInstance("SHA-256").digest(data);
        }catch (NoSuchAlgorithmException e){
            return null;
        }
    }

    private static String byte2hex(byte[] data){
        if (data == null) return null;
        final StringBuilder hexadecimal = new StringBuilder();
        for (final byte b : data){
            hexadecimal.append(String.format("%02x", b));
        }
        return hexadecimal.toString();
    }
}
