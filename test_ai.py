
from keras.models import load_model
from tensorflow.keras.preprocessing import image
import cv2
import requests
import numpy as np
from tensorflow.keras.utils import get_file

class_names = ['Apel', 'Bawang Bombai', 'Bawang Merah', 'Bawang Putih', 'Belimbing', 'Brokoli', 'Buah Naga', 'Cabai Hijau', 'Cabai Merah', 'Cumi-cumi', 'Daging Sapi', 'Daging Unggas', 'Daun Salam', 'Garam', 'Gula', 'Gurita', 'Ikan', 'Jagung', 'Jahe', 'Jambu', 'Jamur', 'Jeroan', 'Jeruk', 'Kacang Hijau', 'Kacang Merah', 'Kacang Panjang', 'Kacang Tanah', 'Keju', 'Kembang Kol', 'Kencur', 'Kentang', 'Kikil', 'Kol', 'Kunyit', 'Kurma', 'Labu Siam', 'Leci', 'Lengkuas', 'Lobster', 'Manggis', 'Melon', 'Merica', 'Mie', 'Nanas', 'Nangka', 'Nasi', 'Oatmeal', 'Paprika', 'Pepaya', 'Petai', 'Pir', 'Pisang', 'Salak', 'Sawi', 'Sawo', 'Selada', 'Seledri', 'Semangka', 'Serai', 'Sirsak', 'Spaghetti', 'Stroberi', 'Susu', 'Telur Ayam', 'Telur Bebek', 'Tempe', 'Tepung Terigu', 'Terong', 'Timun', 'Tofu', 'Tomat', 'Ubi', 'Udang', 'Usus', 'Wortel'] # fill the rest

model = load_model('model.h5')

# gambar = requests.get(url)

def prediksi(ok):


    img = image.load_img(ok, target_size=(224, 224))
    x = image.img_to_array(img)
    x = np.expand_dims(x, axis=0)
    x = x / 255.0

    # classes = np.argmax(model.predict(img), axis = 0)
    classes = model.predict(x)

    top_indices = classes[0].argsort()[-5:][::-1]
    top_accuracies = classes[0][top_indices]
    top_labels = [class_names[i] for i in top_indices]

    # Print the results
    for label, accuracy in zip(top_labels, top_accuracies):
        print(f"{label} [accuracy: {accuracy:.2f}]")

    print(top_labels[0])
    print(top_accuracies[0])
    return top_labels[0], top_accuracies[0]
# f"""
#     nama : "{top_labels[0]},
#     accuracy : "{top_accuracies[0]}"
# """

# if __name__ == '__main__':
#     prediksi(url)