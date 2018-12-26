Related blog [article](https://medium.com/@namar/high-performance-image-classification-with-react-native-336db0a96cd)

For more information, see [react-native-camera](https://github.com/react-native-community/react-native-camera). All `react-native-camera` props/features should also work with this.

# Real time image classification with React Native

Earlier attempts at Image classification over React Native involved sending image data to the model classifier by sending the image over the bridge or storing the image to disk and accessing the image on the native side. Here's an attempt at live image classification by processing from the camera feed on the native side and getting the output as a byte stream on the JS side using the react-native-camera-tflite library.

Huge shout-out to the people over at [react-native-camera](https://github.com/react-native-community/react-native-camera). This is essentially just a fork of their awesome work.

Note: This is currently developed only for Android but could be implemented for iOS. (See [here](https://github.com/jigsawxyz/react-native-coreml-image) for a CoreML implementation on iOS).

To start, let's create an empty react native project:

```
react-native init mobilenetapp
cd mobilenet-app
```

Let's add our dependencies:

```
npm i react-native-camera-tflite
react-native link react-native-camera-tflite
```

Follow the install instructions (for android. Same as react-native-camera):

1. Insert the following lines inside the dependencies block in android/build.gradle:

```
    ...
    allprojects {
        repositories {
            maven { url "[https://jitpack.io](https://jitpack.io)" }  
            maven { url "[https://maven.google.com](https://maven.google.com)" }

    ...
    ...
    ext {
        compileSdkVersion           = 26
        targetSdkVersion            = 26
        buildToolsVersion           = "26.0.2"
        googlePlayServicesVersion   = "12.0.1"
        supportLibVersion           = "27.1.0"
    }
```

2. Insert the following lines inside android/app/build.gradle

    android {
        ...
        aaptOptions {
            noCompress "tflite"
            noCompress "lite"
        }
    ...

Now let's use the download our model file from [here](http://download.tensorflow.org/models/mobilenet_v1_2018_08_02/mobilenet_v1_1.0_224_quant.tgz), decompress it, and copy over the mobilenet_v1_1.0_224_quant.tflite file over to our project.

```
    mkdir -p ./android/app/src/main/assets
    cp mobilenet_v1_1.0_224_quant.tflite ./android/app/src/main/assets
```

Add [this](https://gist.github.com/ppsreejith/1016f74f3c0cc95c121668904da67900) file to your project root directory as Output.json

Replace the content of App.js in your project root directory with the following:

```
    import React, {Component} from 'react';
    import {StyleSheet, Text, View } from 'react-native';
    import { RNCamera } from 'react-native-camera-tflite';
    import outputs from './Output.json';
    import _ from 'lodash';

    let _currentInstant = 0;

    export default class App extends Component {
      constructor(props) {
        super(props);
        this.state = {
          time: 0,
          output: ""
        };
      }

    processOutput({data}) {
        const probs = _.map(data, item => _.round(item/255.0, 0.02));
        const orderedData = _.chain(data).zip(outputs).orderBy(0, 'desc').map(item => [_.round(item[0]/255.0, 2), item[1]]).value();
        const outputData = _.chain(orderedData).take(3).map(item => `${item[1]}: ${item[0]}`).join('\n').value();
        const time = Date.now() - (_currentInstant || Date.now());
        const output = `Guesses:\n${outputData}\nTime:${time} ms`;
        this.setState(state => ({
          output
        }));
        _currentInstant = Date.now();
      }
      
      render() {
        const modelParams = {
          file: "mobilenet_v1_1.0_224_quant.tflite",
          inputDimX: 224,
          inputDimY: 224,
          outputDim: 1001,
          freqms: 0
        };
        return (
          <View style={styles.container}>
            <RNCamera
                ref={ref => {
                    this.camera = ref;
                  }}
                style = {styles.preview}
                type={RNCamera.Constants.Type.back}
                flashMode={RNCamera.Constants.FlashMode.on}
                permissionDialogTitle={'Permission to use camera'}
                permissionDialogMessage={'We need your permission to use your camera phone'}
                onModelProcessed={data => this.processOutput(data)}
                modelParams={modelParams}
            >
              <Text style={styles.cameraText}>{this.state.output}</Text>
            </RNCamera>
          </View>
        );
      }
    }

    const styles = StyleSheet.create({
      container: {
        flex: 1,
        flexDirection: 'column',
        backgroundColor: 'black'
      },
      preview: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center'
      },
      cameraText: {
        color: 'white',
        fontSize: 18,
        fontWeight: 'bold'
      }
    });
# React Native Camera [![Backers on Open Collective](https://opencollective.com/react-native-camera/backers/badge.svg)](#backers) [![Sponsors on Open Collective](https://opencollective.com/react-native-camera/sponsors/badge.svg)](#sponsors) [![npm version](https://badge.fury.io/js/react-native-camera.svg)](http://badge.fury.io/js/react-native-camera) [![npm downloads](https://img.shields.io/npm/dm/react-native-camera.svg)](https://www.npmjs.com/package/react-native-camera)

## Docs
Follow our docs here [https://react-native-community.github.io/react-native-camera/](https://react-native-community.github.io/react-native-camera/)

## Sponsors

If you use this library on your commercial/personal projects, you can help us by funding the work on specific issues that you choose by using IssueHunt.io!

This gives you the power to prioritize our work and support the project contributors. Moreover it'll guarantee the project will be updated and maintained in the long run.

[![issuehunt-image](https://issuehunt.io/static/embed/issuehunt-button-v1.svg)](https://issuehunt.io/repos/33218414)

## react-native-camera for enterprise

Available as part of the Tidelift Subscription

The maintainers of react-native-camera and thousands of other packages are working with Tidelift to deliver commercial support and maintenance for the open source dependencies you use to build your applications. Save time, reduce risk, and improve code health, while paying the maintainers of the exact dependencies you use. [Learn more.](https://tidelift.com/subscription/pkg/npm-react-native-camera?utm_source=npm-react-native-camera&utm_medium=referral&utm_campaign=enterprise&utm_term=repo)

## Open Collective

You can also fund this project using open collective

### Backers

Support us with a monthly donation and help us continue our activities. [[Become a backer](https://opencollective.com/react-native-camera#backer)]

<a href="https://opencollective.com/react-native-camera/backer/0/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/0/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/1/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/1/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/2/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/2/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/3/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/3/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/4/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/4/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/5/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/5/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/6/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/6/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/7/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/7/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/8/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/8/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/9/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/9/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/10/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/10/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/11/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/11/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/12/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/12/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/13/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/13/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/14/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/14/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/15/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/15/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/16/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/16/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/17/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/17/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/18/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/18/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/19/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/19/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/20/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/20/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/21/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/21/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/22/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/22/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/23/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/23/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/24/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/24/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/25/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/25/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/26/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/26/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/27/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/27/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/28/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/28/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/backer/29/website" target="_blank"><img src="https://opencollective.com/react-native-camera/backer/29/avatar.svg"></a>

### Sponsors

Become a sponsor and get your logo on our README on Github with a link to your site. [[Become a sponsor](https://opencollective.com/react-native-camera#sponsor)]

<a href="https://opencollective.com/react-native-camera/sponsor/0/website" target="_blank"><img src="https://opencollective.com/react-native-camera/sponsor/0/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/sponsor/1/website" target="_blank"><img src="https://opencollective.com/react-native-camera/sponsor/1/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/sponsor/2/website" target="_blank"><img src="https://opencollective.com/react-native-camera/sponsor/2/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/sponsor/3/website" target="_blank"><img src="https://opencollective.com/react-native-camera/sponsor/3/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/sponsor/4/website" target="_blank"><img src="https://opencollective.com/react-native-camera/sponsor/4/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/sponsor/5/website" target="_blank"><img src="https://opencollective.com/react-native-camera/sponsor/5/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/sponsor/6/website" target="_blank"><img src="https://opencollective.com/react-native-camera/sponsor/6/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/sponsor/7/website" target="_blank"><img src="https://opencollective.com/react-native-camera/sponsor/7/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/sponsor/8/website" target="_blank"><img src="https://opencollective.com/react-native-camera/sponsor/8/avatar.svg"></a>
<a href="https://opencollective.com/react-native-camera/sponsor/9/website" target="_blank"><img src="https://opencollective.com/react-native-camera/sponsor/9/avatar.svg"></a>

The comprehensive camera module for React Native.

Supports:

- photographs.
- videos
- face detection (Android & iOS only)
- barcode scanning
- text recognition (optional installation for iOS using CocoaPods)

### Example import

```jsx
import { RNCamera, FaceDetector } from 'react-native-camera';
```

#### How to use master branch?

We recommend using the releases from npm, however if you need some features that are not published on npm yet you can install react-native-camera from git.

**yarn**: `yarn add react-native-camera@git+https://git@github.com/react-native-community/react-native-camera.git`

**npm**: `npm install --save react-native-camera@git+https://git@github.com/react-native-community/react-native-camera.git`

### Contributing

- Pull Requests are welcome, if you open a pull request we will do our best to get to it in a timely manner
- Pull Request Reviews are even more welcome! we need help testing, reviewing, and updating open PRs
- If you are interested in contributing more actively, please contact me (same username on Twitter, Facebook, etc.) Thanks!
- We are now on [Open Collective](https://opencollective.com/react-native-camera#sponsor)! Contributions are appreciated and will be used to fund core contributors. [more details](#open-collective)
- If you want to help us coding, join Expo slack https://slack.expo.io/, so we can chat over there. (#react-native-camera)

##### Permissions

To use the camera,

1) On Android you must ask for camera permission:

```java
  <uses-permission android:name="android.permission.CAMERA" />
```

To enable `video recording` feature you have to add the following code to the `AndroidManifest.xml`:

```java
  <uses-permission android:name="android.permission.RECORD_AUDIO"/>
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

![5j2jduk](https://cloud.githubusercontent.com/assets/2302315/22190752/6bc6ccd0-e0da-11e6-8e2f-6f22a3567a57.gif)

2) On iOS, you must update Info.plist with a usage description for camera

```xml
...
<key>NSCameraUsageDescription</key>
<string>Your own description of the purpose</string>
...
	
```

We're done! Run your app with the following command.
For more information on installation, please refer to [installation requirements](./docs/installation.md#requirements).

```
    react-native run-android
```
For general introduction, please take a look into this [RNCamera](./docs/RNCamera.md).

![Image Classification FTW!](https://cdn-images-1.medium.com/max/2000/1*9gvd0iTkVIlI4FvfGim6gg.gif)*Image Classification FTW!*
## Security contact information

To convert this to a hotdog not-hotdog app, just replace the processOutput function above with the following:
To report a security vulnerability, please use the

```
    processOutput({data}) {
      const isHotDogProb = data[935];
      const isHotDog = isHotDogProb > 0.2 ? "HotDog" : "Not HotDog";
      const time = Date.now() - (_currentInstant || Date.now());
      const output = `${isHotDog}\nTime:${time} ms`;
      this.setState(state => ({
       output
      }));
     _currentInstant = Date.now();
    }
```
[Tidelift security contact](https://tidelift.com/security).

Run your app with the following command.

```
    react-native run-android
```
![It's HotDog](https://cdn-images-1.medium.com/max/2000/1*JUPsOWLBvBwQoP4jHwv__A.gif)*It‚Äôs HotDog*

Jian Yang would be proud :)

This project has a lot of rough edges. I hope to clean up this up a lot more in the coming days. The rest of the features are the same as `react-native-camera`.

Links:
[Github](https://github.com/ppsreejith/react-native-camera-tflite)
[Demo App](https://github.com/ppsreejith/tflite-demo)
[npm](https://www.npmjs.com/package/react-native-camera-tflite)
Tidelift will coordinate the fix and disclosure.
