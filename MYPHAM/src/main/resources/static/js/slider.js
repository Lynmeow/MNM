
// slider.js

let currentIndex = 0; // Chỉ mục hiện tại

const items = document.querySelectorAll('.carousel-item'); // Lấy tất cả các item trong carousel
const totalItems = items.length; // Tổng số phần tử trong carousel

// Nút điều khiển trước
document.querySelector('.prev-btn').addEventListener('click', () => {
    if (currentIndex > 0) {
        currentIndex--;
    } else {
        currentIndex = totalItems - 1; // Nếu ở item đầu tiên, quay lại item cuối
    }
    updateCarousel();
});

// Nút điều khiển tiếp theo
document.querySelector('.next-btn').addEventListener('click', () => {
    if (currentIndex < totalItems - 1) {
        currentIndex++;
    } else {
        currentIndex = 0; // Nếu ở item cuối, quay lại item đầu
    }
    updateCarousel();
});

// Cập nhật carousel
function updateCarousel() {
    const carousel = document.querySelector('.carousel');
    carousel.style.transform = `translateX(-${currentIndex * 20}%)`; // Di chuyển lưới dựa trên chỉ mục hiện tại
}
