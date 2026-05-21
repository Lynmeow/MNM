// Hàm áp dụng mã giảm giá
function applyDiscount() {
    const maKM = document.getElementById('maKM').value.trim();
    const applyBtn = document.querySelector('.btn-apply');
    
    if (!maKM) {
        showMessage('Vui lòng nhập mã giảm giá!', 'error');
        return;
    }
    
    // Disable button và hiển thị loading
    applyBtn.disabled = true;
    applyBtn.textContent = 'Đang xử lý...';
    
    // Gửi request đến server để kiểm tra mã giảm giá
    fetch('/thanhtoan/ap-dung-ma-giam-gia', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ maKM: maKM })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showMessage(`✓ Áp dụng mã giảm giá thành công! Giảm ${data.phanTram}% (${formatCurrency(data.soTienGiam)} VND)`, 'success');
            updateTotalPrice(data.tongTienSauGiam, data.soTienGiam, data.tongTienGoc);
            
            // Disable input sau khi áp dụng thành công
            document.getElementById('maKM').disabled = true;
            applyBtn.textContent = 'Đã áp dụng';
        } else {
            showMessage('✗ ' + (data.message || 'Mã giảm giá không hợp lệ!'), 'error');
            applyBtn.disabled = false;
            applyBtn.textContent = 'Áp dụng';
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showMessage('✗ Có lỗi xảy ra khi áp dụng mã giảm giá!', 'error');
        applyBtn.disabled = false;
        applyBtn.textContent = 'Áp dụng';
    });
}

// Hiển thị thông báo
function showMessage(message, type) {
    const messageDiv = document.getElementById('discount-message');
    messageDiv.textContent = message;
    messageDiv.className = 'discount-message ' + type;
}

// Cập nhật tổng tiền sau khi áp dụng giảm giá
function updateTotalPrice(tongTienSauGiam, soTienGiam, tongTienGoc) {
    const tongTienContainer = document.querySelector('.tong-tien');
    
    // Tạo lại toàn bộ nội dung
    tongTienContainer.innerHTML = `
        <div class="tong-tien-row">
            <span>Tạm tính:</span>
            <span class="price">${formatCurrency(tongTienGoc)} VND</span>
        </div>
        <div class="tong-tien-row discount-row">
            <span>Giảm giá:</span>
            <span class="discount-amount">-${formatCurrency(soTienGiam)} VND</span>
        </div>
        <div class="tong-tien-row total-row">
            <span><strong>Tổng cộng:</strong></span>
            <span class="price total-price">
                <strong>${formatCurrency(tongTienSauGiam)} VND</strong>
            </span>
        </div>
    `;
    
    // Thêm hiệu ứng animation
    tongTienContainer.style.animation = 'pulse 0.5s ease';
    setTimeout(() => {
        tongTienContainer.style.animation = '';
    }, 500);
}

// Format số tiền
function formatCurrency(amount) {
    return amount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// Validate form trước khi submit
document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('.payment-form');
    
    if (form) {
        form.addEventListener('submit', function(e) {
            // Validate số điện thoại
            const soDienThoai = document.getElementById('soDienThoai').value.trim();
            const phonePattern = /^[0-9]{10,11}$/;
            
            if (!phonePattern.test(soDienThoai)) {
                e.preventDefault();
                alert('Số điện thoại không hợp lệ! Vui lòng nhập 10-11 chữ số.');
                document.getElementById('soDienThoai').focus();
                return false;
            }
            
            // Validate địa chỉ
            const diaChi = document.getElementById('diaChi').value.trim();
            if (diaChi.length < 10) {
                e.preventDefault();
                alert('Vui lòng nhập địa chỉ đầy đủ và chi tiết!');
                document.getElementById('diaChi').focus();
                return false;
            }
            
            // Hiển thị loading
            const submitBtn = form.querySelector('.btn-submit');
            submitBtn.disabled = true;
            submitBtn.textContent = 'Đang xử lý...';
            
            // Form sẽ được submit nếu validation pass
            return true;
        });
    }
    
    // Thêm animation khi scroll
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    });
    
    document.querySelectorAll('.giohang, .payment-form').forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(20px)';
        el.style.transition = 'all 0.5s ease';
        observer.observe(el);
    });
    
    // Xử lý enter key trên input mã giảm giá
    const maKMInput = document.getElementById('maKM');
    if (maKMInput) {
        maKMInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                applyDiscount();
            }
        });
    }
});