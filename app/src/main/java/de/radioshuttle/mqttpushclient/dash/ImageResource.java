/*
 *	$Id$
 *	This is an unpublished work copyright (c) 2019 HELIOS Software GmbH
 *	30827 Garbsen, Germany.
 */

package de.radioshuttle.mqttpushclient.dash;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;

import de.radioshuttle.net.Cmd;
import de.radioshuttle.utils.HeliosUTF8Decoder;
import de.radioshuttle.utils.HeliosUTF8Encoder;
import de.radioshuttle.utils.Utils;

public final class ImageResource {
    public Drawable drawable;
    /* resource id for internal images */
    public int id;
    public String uri;
    public String label;


    public static class LabelComparator implements Comparator<ImageResource> {

        @Override
        public int compare(ImageResource r1, ImageResource r2) {
            String s1, s2;
            if (r1 != null && r1.label != null) {
                s1 = r1.label;
            } else {
                s1 = "";
            }
            if (r2 != null && r2.label != null) {
                s2 = r2.label;
            } else {
                s2 = "";
            }
            return s1.compareToIgnoreCase(s2);
        }
    }

    public static class IDComparator implements Comparator<ImageResource> {

        @Override
        public int compare(ImageResource r1, ImageResource r2) {
            int i1, i2, r = 0;
            if (r1 != null) {
                i1 = r1.id;
            } else {
                i1 = Integer.MIN_VALUE;
            }
            if (r2 != null) {
                i2 = r2.id;
            } else {
                i2 = Integer.MIN_VALUE;
            }
            if (i1 < i2) {
                r = -1;
            } else if (i1 > i2) {
                r = 1;
            }
            return r;
        }
    }


    public static boolean isInternalResource(String uri) {
        return !Utils.isEmpty(uri) && IconHelper.INTENRAL_ICONS.containsKey(uri);
    }

    public static boolean isImportedResource(String uri) {
        return !Utils.isEmpty(uri) && uri.toLowerCase().startsWith("res://imported/");
    }

    public static boolean isUserResource(String uri) {
        return !Utils.isEmpty(uri) && uri.toLowerCase().startsWith("res://user/");
    }

    public static String buildUserResourceURI(String resourceName){
        if (resourceName != null) {
            try {
                resourceName = "res://user/" + Utils.urlEncode(resourceName);
            } catch(UnsupportedEncodingException e) {
            }
        }
        return resourceName;
    }

    public static String getURIPath(String uri) {
        String uriPath = null;
        URI u = null;
        try {
            if (uri != null) {
                u = new URI(uri);
                uriPath = u.getPath();
                if (uriPath.startsWith("/")) {
                    uriPath = uriPath.substring(1);
                }
            }
        } catch(Exception e) {}
        return uriPath;
    }

    public static String decodeFilename(String filename) {
        if (filename != null) {
            HeliosUTF8Decoder dec = new HeliosUTF8Decoder();
            filename = dec.format(filename);
        }
        return filename;
    }

    public static String encodeFilename(String filename) {
        if (filename != null) {
            HeliosUTF8Encoder enc = new HeliosUTF8Encoder();
            filename = enc.format(filename);
        }
        return filename;
    }

    public static String removeExtension(String file) {
        if (file != null) {
            int idx = file.lastIndexOf('.');
            if (idx != -1 && idx > 0) {
                file = file.substring(0, idx);
            }
        }
        return file;
    }

    public static String removeImportedFilePrefix(String file) {
        if (file != null) {
            int idx = file.indexOf('_');
            if (idx != -1) {
                file = file.substring(idx + 1);
            }
        }
        return file;
    }

    public static boolean isExternalResource(String uri) {
        return isUserResource(uri) || isImportedResource(uri);
    }

    public static BitmapDrawable loadExternalImage(Context context, String uri) throws URISyntaxException, UnsupportedEncodingException {
        File dir = null;
        String name = null;
        if (isImportedResource(uri)) {
            dir = ImportFiles.getImportedFilesDir(context);
            name = getURIPath(uri);
        } else if (isUserResource(uri)) {
            dir = ImportFiles.getUserFilesDir(context);
            name = getURIPath(uri) + '.' + Cmd.DASH512_PNG;
        }
        String path = dir.getAbsolutePath();
        if (!path.endsWith("/")) {
            path += "/";
        }
        path += name;

        Bitmap bm = BitmapFactory.decodeFile(path);
        BitmapDrawable bd = null;
        if (bm != null) {
            bd = new BitmapDrawable(context.getResources(), bm);
        }
        return bd;
    }

    private final static String TAG = ImageResource.class.getSimpleName();
}