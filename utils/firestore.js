const { Firestore } = require('@google-cloud/firestore');
 
const db = new Firestore();

async function storeData(id, data) {
  const predictCollection = db.collection('predictions');
  return predictCollection.doc(id).set(data);
}

async function getData() {
  const predictCollection = db.collection('predictions');
  const snapshot = await predictCollection.get();

  if (snapshot.empty) {
    // console.log('No matching documents.');
    return null;
  }

  const predictionsList = [];
  snapshot.forEach(doc => {
    let datares = doc.data();
    predictionsList.push({
      id: datares.id,
      history: datares
    });
  });

  return predictionsList;
}
 
module.exports = {storeData, getData};