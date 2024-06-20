# Importing flask module in the project is mandatory
# An object of Flask class is our WSGI application.
from flask import Flask, redirect, url_for, request
import base64
import json
import test_ai as test
from PIL import Image
from io import BytesIO
import requests

# Flask constructor takes the name of 
# current module (__name__) as argument.
app = Flask(__name__)

# The route() function of the Flask class is a decorator, 
# which tells the application which URL should call 
# the associated function.
@app.route('/', methods=['POST', 'GET'])
# ‘/’ URL is bound with hello_world() function.
def hello_world():
    if request.method == 'POST':
        user = request.files['gambar']
        #  img = Image.open(io.BytesIO(request_object_content))
        # oke = base64.b64encode(user.read())
        # print(user.read())
        oke = BytesIO(user.read())
        nama, akurasi = test.prediksi(oke)
        print(nama)
        print(akurasi)
        # akurasi = (str(akurasi)).replace('.', '-')
        akurasi = str(akurasi)
        data = requests.get(f'http://api.aexon.cloud/api/v1/recipe/ingredients?query={nama}')
        # type(data.text)
        data = json.loads(data.text)
        # print(data.text)
        # print(type(data))
        data['akurasi'] = akurasi
        # temp = data['resep']
        # data.pop('resep')
        # data['resep'] = temp
        # print(type(data.text))
        return data
    return '''
    <h1>Upload Gambar untuk prediksi resep</h1>
    <form method="post" enctype="multipart/form-data">
      <input type="file" name="gambar">
      <input type="submit">
    </form>
    '''

# main driver function
if __name__ == '__main__':

    # run() method of Flask class runs the application 
    # on the local development server.
    app.run(port=8090, host='0.0.0.0')