const imageInput = document.getElementById('imageFile');
const imagePreview = document.getElementById('imagePreview');

imageInput.addEventListener('change', () => {
  if (imageInput.files[0]) {
    const reader = new FileReader();
    reader.onload = () => {
      imagePreview.innerHTML =
        `<img src="${reader.result}" class="img-fluid mb-3">`;
    };
    reader.readAsDataURL(imageInput.files[0]);
  } else {
    imagePreview.innerHTML = '';
  }
});