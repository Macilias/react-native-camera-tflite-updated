package org.reactnative.camera.tasks;

import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import android.os.SystemClock;
import java.nio.ByteBuffer;

import java.util.concurrent.TimeUnit;

public class ModelProcessorAsyncTask extends android.os.AsyncTask<Void, Void, ByteBuffer> {

  private ModelProcessorAsyncTaskDelegate mDelegate;
  private FirebaseModelInterpreter mModelProcessor;
  private ByteBuffer mInputBuf;
  private ByteBuffer mOutputBuf;
  private int mModelMaxFreqms;
  private int mWidth;
  private int mHeight;
  private int mRotation;

  public ModelProcessorAsyncTask(
      ModelProcessorAsyncTaskDelegate delegate,
      FirebaseModelInterpreter modelProcessor,
      ByteBuffer inputBuf,
      ByteBuffer outputBuf,
      int modelMaxFreqms,
      int width,
      int height,
      int rotation
  ) {
    mDelegate = delegate;
    mModelProcessor = modelProcessor;
    mInputBuf = inputBuf;
    mOutputBuf = outputBuf;
    mModelMaxFreqms = modelMaxFreqms;
    mWidth = width;
    mHeight = height;
    mRotation = rotation;
  }

  private FirebaseModelInputOutputOptions createInputOutputOptions() throws FirebaseMLException {
    // [START mlkit_create_io_options]
    FirebaseModelInputOutputOptions inputOutputOptions =
            new FirebaseModelInputOutputOptions.Builder()
                    .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 224, 224, 3})
                    .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 5})
                    .build();
    // [END mlkit_create_io_options]

    return inputOutputOptions;
  }
    
  @Override
  protected ByteBuffer doInBackground(Void... ignored) {
    if (isCancelled() || mDelegate == null || mModelProcessor == null) {
      return null;
    }
    long startTime = SystemClock.uptimeMillis();
    try {
      mInputBuf.rewind();
      mOutputBuf.rewind();
      FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
              .add(mInputBuf)  // add() as many input arrays as your model requires
              .build();
      FirebaseModelInputOutputOptions inputOutputOptions = createInputOutputOptions();
      mModelProcessor.run(inputs, inputOutputOptions);
    } catch (Exception e) {}
    try {
      if (mModelMaxFreqms > 0) {
        long endTime = SystemClock.uptimeMillis();
        long timeTaken = endTime - startTime;
        if (timeTaken < mModelMaxFreqms) {
          TimeUnit.MILLISECONDS.sleep(mModelMaxFreqms - timeTaken);
        }
      }
    } catch (Exception e) {}
    return mOutputBuf;
  }

  @Override
  protected void onPostExecute(ByteBuffer data) {
    super.onPostExecute(data);

    if (data != null) {
      mDelegate.onModelProcessed(data, mWidth, mHeight, mRotation);
    }
    mDelegate.onModelProcessorTaskCompleted();
  }
}
