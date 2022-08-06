package com.harshbhanderi.photoeditor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImage;
import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


public class EditScreen extends AppCompatActivity {
    static {
        System.loadLibrary("NativeImageProcessor");
    }


    ImageView image;
    Button backBtn, nextBtn, cropBtn, brightBtn, contrastBtn, saturationBtn, normalBtn,
            lightBtn, bluemessBtn, starlitbtn, awestruckBtn, limeBtn, nightBtn;
    SeekBar SeekBright, SeekContrast, SeekSaturation;
    Bitmap bim,bim2;
    private ColorMatrix colorMatrix;
    private ColorMatrixColorFilter cmFilter;
    private Paint cmPaint;
    private Bitmap canvasBitmap;
    private Canvas cv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_screen);

        getSupportActionBar().hide();

        //Images
        image = findViewById(R.id.image);

        //Buttons
        backBtn = findViewById(R.id.back);
        nextBtn = findViewById(R.id.next);
        cropBtn = findViewById(R.id.crop);
        brightBtn = findViewById(R.id.bright);
        contrastBtn = findViewById(R.id.contrast);
        saturationBtn = findViewById(R.id.saturation);
        normalBtn = findViewById(R.id.normal);
        lightBtn = findViewById(R.id.light);
        bluemessBtn = findViewById(R.id.bluemess);
        starlitbtn = findViewById(R.id.starlit);
        awestruckBtn = findViewById(R.id.awe);
        limeBtn = findViewById(R.id.lime);
        nightBtn = findViewById(R.id.night);

        //Seekbars
        SeekBright = findViewById(R.id.seekbright);
        SeekContrast = findViewById(R.id.seekcontrast);
        SeekSaturation = findViewById(R.id.seeksaturation);

        //Seekbar Properties
        SeekBright.setMax(510);
        SeekContrast.setMax(10);
        SeekSaturation.setMax(510);
        SeekBright.setProgress(255);
        SeekContrast.setProgress(2);
        SeekSaturation.setProgress(255);

//        AlertDialog alertDialog = new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setMessage("Due to various changes in version and library\nFilters are not working directly\nTo use filters please make minor change in\nBrightness.\nWe will fix this issue as soon as possible\n thank you")
//                .setPositiveButton(android.R.string.ok, null)
//                .show();

        //Get Image from MainActivity
        Intent intent = getIntent();
        Uri imguri = intent.getData();
        image.setImageURI(imguri);
        bim = ((BitmapDrawable) image.getDrawable()).getBitmap();
        bim2 = ((BitmapDrawable) image.getDrawable()).getBitmap();
        image.setImageBitmap(bim);

        //Click on Back Button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Click on Crop Button
        cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SeekBright.setVisibility(View.INVISIBLE);
                SeekContrast.setVisibility(View.INVISIBLE);
                CropImage.activity(imguri)
                        .start(EditScreen.this);
                bim = ((BitmapDrawable) image.getDrawable()).getBitmap();
            }
        });

        normalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditScreen.this, "Under Development", Toast.LENGTH_SHORT).show();
            }
        });

        //Click on Bright Button
        bim= ((BitmapDrawable) image.getDrawable()).getBitmap();
        brightBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SeekBright.setVisibility(View.VISIBLE);
                SeekContrast.setVisibility(View.INVISIBLE);
                SeekSaturation.setVisibility(View.INVISIBLE);
                SeekBright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        Bitmap new_bm = changeBitmapContrastBrightness(bim, (float) (progress - 255));
                        image.setImageBitmap(new_bm);
                        SeekBright.setProgress(progress);
                     }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        });

        //Click on Contrast Button
        contrastBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                bim = ((BitmapDrawable) image.getDrawable()).getBitmap();
                SeekContrast.setVisibility(View.VISIBLE);
                SeekBright.setVisibility(View.INVISIBLE);
                SeekSaturation.setVisibility(View.INVISIBLE);
                SeekContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        Bitmap new_bm = changeBitmapContrastBrightness(bim, (float) (progress - 1), (float) (SeekContrast.getProgress() - 255));
                        image.setImageBitmap(new_bm);
                        SeekContrast.setProgress(progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

            }
        });

        //Click on Saturation Button
        colorMatrix = new ColorMatrix();
        cmFilter = new ColorMatrixColorFilter(this.colorMatrix);
        saturationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SeekSaturation.setVisibility(View.VISIBLE);
                SeekContrast.setVisibility(View.INVISIBLE);
                SeekBright.setVisibility(View.INVISIBLE);
                SeekSaturation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        cmPaint = new Paint();
                        cmPaint.setColorFilter(cmFilter);
                        canvasBitmap = Bitmap.createBitmap(bim.getWidth(), bim.getHeight(), Bitmap.Config.ARGB_8888);
                        cv = new Canvas(canvasBitmap);
                        colorMatrix.setSaturation(progress / (float) 100);
                        cmFilter = new ColorMatrixColorFilter(colorMatrix);
                        cmPaint.setColorFilter(cmFilter);
                        cv.drawBitmap(bim, 0, 0, cmPaint);
                        image.setImageBitmap(canvasBitmap);
                        SeekSaturation.setProgress(progress);

                    }
                });
            }
        });
        lightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disable();
                bim = ((BitmapDrawable) image.getDrawable()).getBitmap();
                Toast.makeText(EditScreen.this, "Be aware, Filter will work as many as time you will click on it. It cannot be undo as well. To remove all Effect use normal button", Toast.LENGTH_SHORT).show();
                Bitmap filterbit = convertToMutable(bim);
                Filter myFilter = new Filter();
                myFilter.addSubFilter(new BrightnessSubFilter(30));
                myFilter.addSubFilter(new ContrastSubFilter(1.1f));
                Bitmap outputImage = myFilter.processFilter(filterbit);
                image.setImageBitmap(outputImage);
            }
        });

        bluemessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disable();
                bim = ((BitmapDrawable) image.getDrawable()).getBitmap();
                Toast.makeText(EditScreen.this, "Be aware, Filter will work as many as time you will click on it. It cannot be undo as well. To remove all Effect use normal button", Toast.LENGTH_SHORT).show();
                Bitmap filterbit = convertToMutable(bim);
                Filter fooFilter = SampleFilters.getBlueMessFilter();
                Bitmap outputImage = fooFilter.processFilter(filterbit);
                image.setImageBitmap(outputImage);
            }
        });


        starlitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disable();
                bim = ((BitmapDrawable) image.getDrawable()).getBitmap();
                Toast.makeText(EditScreen.this, "Be aware, Filter will work as many as time you will click on it. It cannot be undo as well. To remove all Effect use normal button", Toast.LENGTH_SHORT).show();
                Bitmap filterbit = convertToMutable(bim);
                Filter fooFilter = SampleFilters.getStarLitFilter();
                Bitmap outputImage = fooFilter.processFilter(filterbit);
                image.setImageBitmap(outputImage);
             }
        });

        awestruckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disable();
                bim = ((BitmapDrawable) image.getDrawable()).getBitmap();
                Toast.makeText(EditScreen.this, "Be aware, Filter will work as many as time you will click on it. It cannot be undo as well. To remove all Effect use normal button", Toast.LENGTH_SHORT).show();
                Bitmap filterbit = convertToMutable(bim);
                Filter fooFilter = SampleFilters.getAweStruckVibeFilter();
                Bitmap outputImage = fooFilter.processFilter(filterbit);
                image.setImageBitmap(outputImage);
             }
        });

        limeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disable();
                bim = ((BitmapDrawable) image.getDrawable()).getBitmap();
                Bitmap filterbit = convertToMutable(bim);
                Filter fooFilter = SampleFilters.getLimeStutterFilter();
                Bitmap outputImage = fooFilter.processFilter(filterbit);
                image.setImageBitmap(outputImage);
             }
        });

        nightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disable();
                bim = ((BitmapDrawable) image.getDrawable()).getBitmap();
                Toast.makeText(EditScreen.this, "Be aware, Filter will work as many as time you will click on it. It cannot be undo as well. To remove all Effect use normal button", Toast.LENGTH_SHORT).show();
                Bitmap filterbit = convertToMutable(bim);
                Filter fooFilter = SampleFilters.getNightWhisperFilter();
                Bitmap outputImage = fooFilter.processFilter(filterbit);
                image.setImageBitmap(outputImage);

            }
        });

        bim = ((BitmapDrawable) image.getDrawable()).getBitmap();
        //Click on Next Button
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String savedImageURL = MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        bim,
                        null, null

                );
                Uri savedImageURI = Uri.parse(savedImageURL);
                Intent intent1 = new Intent(EditScreen.this, result.class);
                intent1.setData(savedImageURI);
                startActivity(intent1);
            }
        });


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                image.setImageURI(resultUri);
            }
        }
    }

    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        1, 0, 0, 0, brightness,
                        0, 1, 0, 0, brightness,
                        0, 0, 1, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, 1,
                        0, contrast, 0, 0, 1,
                        0, 0, contrast, 0, 1,
                        0, 0, 0, 1, contrast
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
            imgIn.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle();
            System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgIn;
    }

    public void disable()
    {
        SeekBright.setVisibility(View.INVISIBLE);
        SeekContrast.setVisibility(View.INVISIBLE);
        SeekSaturation.setVisibility(View.INVISIBLE);
    }
}















