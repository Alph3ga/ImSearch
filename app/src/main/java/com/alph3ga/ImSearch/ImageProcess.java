package com.alph3ga.ImSearch;

import android.net.Uri;

import org.opencv.core.Core;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageProcess {
      static Imgcodecs imCode;

      public static void init(){
            //Loading the OpenCV core library
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            imCode= new Imgcodecs();
      }

      public static Uri processImage(Uri savedUri){

      }
}
