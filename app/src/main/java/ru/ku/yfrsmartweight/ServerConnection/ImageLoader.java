package ru.ku.yfrsmartweight.ServerConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class ImageLoader {
    enum Mode {
        loadInStorage,
        CreatingImageView
    }

    private static final String LOG_TAG = "Log_in_ImageLoader";

    public ImageLoader() {
    }

    // Режим для загрузки фотографий в постоянную память
    public boolean load(String filename, byte[] bytes, Context mContext, Mode mode) {
        //Проверка режима
        if (mode == Mode.loadInStorage) {

            //Режим загрузки фотографий в память

            Log.d(LOG_TAG, "LoadInStorage");
            //Проверка директории на возможность чтения/записи
            if (!isExternalStorageWritable())
                return false;
            // Получение директории с фотографиями
            File folder = getAlbumStorageDir(mContext, "Dish_examples");
            //Проверка отсутсвия файла в директории с фотографиями
            //Создание файла
            String fullFileName = filename + ".jpeg";
            File file = new File(folder, fullFileName);
            if (!file.exists()) {
                file.delete();
                // существует
            }
            // Из массива байт в битмапку
            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            OutputStream fOut = null;
            try {
                //Создает поток вывода для записи файла
                fOut = new FileOutputStream(file);
                // Сохранет картинку в джпеге 100% качества
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
                // регистрация в фотоальбоме. Не нужно, но во время отладки пригодится
                MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                        file.getAbsolutePath(), file.getName(), file.getName());

            } catch (IOException e) {
                e.printStackTrace();
                return false;

            }


        }

        return true;
    }


    // Режим для создания imageView
    public ImageView load(String filename, byte[] bytes, Context mContext, ImageView imageView, Mode mode) {
        Log.e(LOG_TAG, "CreatingImageView");
        if (mode == Mode.CreatingImageView) {
            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(image);
        }
        return imageView;
    }


    /*Установка изображения в ImageView
     * return:
     * 0 - файл не существует
     * 1 - картинка успешно установлена
     * */
    public int setImageView(String fileName, ImageView imageView, Context mContext) {
        // Получение директории с фотографиями
        File folder = getAlbumStorageDir(mContext, "Dish_examples");
        fileName = fileName + ".jpeg";
        File file = new File(folder, fileName);
        if (!file.exists())
            return 0;
        String fullFileName = file.getAbsolutePath();
        Bitmap bmp = BitmapFactory.decodeFile(fullFileName);
        imageView.setImageBitmap(bmp);
        return 1;
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // Get the directory for the app's private pictures directory.
    public File getAlbumStorageDir(Context context, String albumName) {

        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

}