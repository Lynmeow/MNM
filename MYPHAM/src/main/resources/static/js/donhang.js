// ================= XỬ LÝ LINK ĐƠN HÀNG =================

document.addEventListener('DOMContentLoaded', function() {
    const donhangLink = document.querySelector('.donhang-link');
    
    if (donhangLink) {
        donhangLink.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Lấy userId từ session (bạn cần truyền từ backend)
            const userId = getUserId();
            
            if (userId) {
                // Chuyển đến trang đơn hàng với userId
                window.location.href = `/donhang/my-orders?userId=${userId}`;
            } else {
                alert('Vui lòng đăng nhập để xem đơn hàng!');
                window.location.href = '/auth';
            }
        });
        
        // Load số lượng đơn hàng mới (optional)
        loadOrderCount();
    }
});

// Hàm lấy userId từ session/cookie
function getUserId() {
    // CÁCH 1: Từ attribute data đã set trong HTML
    const userElement = document.querySelector('[data-user-id]');
    if (userElement) {
        return userElement.getAttribute('data-user-id');
    }
    
    // CÁCH 2: Từ cookie
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
        const [name, value] = cookie.trim().split('=');
        if (name === 'userId') {
            return value;
        }
    }
    
    // CÁCH 3: Từ localStorage
    return localStorage.getItem('userId');
}

// Hàm load số lượng đơn hàng mới (optional)
function loadOrderCount() {
    const userId = getUserId();
    if (!userId) return;
    
    fetch(`/api/donhang/count?userId=${userId}`)
        .then(response => response.json())
        .then(data => {
            const donhangLink = document.querySelector('.donhang-link');
            if (data.count > 0) {
                donhangLink.setAttribute('data-count', data.count);
            }
        })
        .catch(error => console.log('Error loading order count:', error));
}