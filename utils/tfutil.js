require('dotenv').config();
const tfjs = require('@tensorflow/tfjs-node');

function loadModel() {
  const modelUrl = "file://models/model.json";
  return tfjs.loadGraphModel(modelUrl);
}
 
function predict(model, imageBuffer) {
  const tensor = tfjs.node
    .decodePng(imageBuffer)
    .resizeBilinear([224, 224])
    .toFloat()
    .expandDims(0);
  //const tensors = tfjs.tensor4d(Array.from(tensor.dataSync()),[1,224,224,3]);
  //tensor = imageBuffer;
  return model.predict(tensor).data();;
}
 
module.exports = { loadModel, predict };
