import os
import cv2
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.decomposition import PCA
from sklearn.svm import SVC
from sklearn.model_selection import cross_val_predict, StratifiedKFold
from sklearn.metrics import confusion_matrix, ConfusionMatrixDisplay
from sklearn.manifold import TSNE
from sklearn.preprocessing import LabelEncoder

# 1. Carregar e pré-processar imagens
def load_orl_faces(dataset_path='orl_faces', image_size=(112, 92)):
    X, y, images = [], [], []
    label_encoder = LabelEncoder()
    subjects = sorted(os.listdir(dataset_path))
    all_labels = []

    for subject in subjects:
        subject_path = os.path.join(dataset_path, subject)
        if not os.path.isdir(subject_path):
            continue
        for file in sorted(os.listdir(subject_path)):
            file_path = os.path.join(subject_path, file)
            img = cv2.imread(file_path, cv2.IMREAD_GRAYSCALE)
            img = cv2.resize(img, image_size)
            images.append(img)
            X.append(img.flatten())
            all_labels.append(subject)

    X = np.array(X)
    y = label_encoder.fit_transform(all_labels)
    images = np.array(images)

    return X, y, images, label_encoder

# 2. Aplicar PCA
def apply_pca(X, n_components=50):
    pca = PCA(n_components=n_components, whiten=True, random_state=42)
    X_pca = pca.fit_transform(X)
    return X_pca, pca

# 3. Treinar SVM
def train_svm(X, y):
    model = SVC(kernel='rbf', probability=True, random_state=42)
    model.fit(X, y)
    return model

# 4. Classificar uma imagem nova e visualizar
def predict_and_visualize(model, pca, X_pca, y, images, label_encoder, test_idx=0):
    test_img = images[test_idx]
    test_vector = test_img.flatten().reshape(1, -1)
    test_pca = pca.transform(test_vector)

    predicted_class = model.predict(test_pca)[0]
    predicted_prob = model.predict_proba(test_pca)[0]
    top5_idx = np.argsort(predicted_prob)[::-1][:5]

    # Mostrar imagem de teste
    plt.imshow(test_img, cmap='gray')
    plt.title(f"Classe prevista: {label_encoder.inverse_transform([predicted_class])[0]}")
    plt.axis('off')
    plt.show()

    # Mostrar 9 imagens da classe prevista
    matching_imgs = images[y == predicted_class][:9]
    fig, axes = plt.subplots(3, 3, figsize=(8, 8))
    for ax, img in zip(axes.flatten(), matching_imgs):
        ax.imshow(img, cmap='gray')
        ax.axis('off')
    plt.suptitle(f"Exemplos da Classe Prevista ({label_encoder.inverse_transform([predicted_class])[0]})")
    plt.show()

    # Mostrar Top-5 classes
    print("Top-5 Classes Previstas:")
    for i, idx in enumerate(top5_idx):
        print(f"{i+1}. {label_encoder.inverse_transform([idx])[0]} - Confiança: {predicted_prob[idx]:.4f}")

# Pipeline principal
dataset_path = 'orl_faces'
X, y, images, label_encoder = load_orl_faces(dataset_path)
X_pca, pca_model = apply_pca(X, n_components=50)
svm_model = train_svm(X_pca, y)

# Classificar uma imagem de teste
predict_and_visualize(svm_model, pca_model, X_pca, y, images, label_encoder, test_idx=123)

# Matriz de Confusão
cv = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
y_pred = cross_val_predict(svm_model, X_pca, y, cv=cv)
cm = confusion_matrix(y, y_pred)
ConfusionMatrixDisplay(cm).plot()
plt.title("Matriz de Confusão (5-Fold)")
plt.show()

# Projeção t-SNE
tsne = TSNE(n_components=2, random_state=42)
X_tsne = tsne.fit_transform(X_pca)
plt.figure(figsize=(10, 6))
sns.scatterplot(x=X_tsne[:, 0], y=X_tsne[:, 1], hue=label_encoder.inverse_transform(y), palette='tab20', legend=None)
plt.title("Projeção t-SNE dos vetores PCA")
plt.xlabel("t-SNE 1")
plt.ylabel("t-SNE 2")
plt.show()

