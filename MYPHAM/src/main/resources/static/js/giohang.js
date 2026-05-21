// ==================== GIỎ HÀNG JAVASCRIPT (LOCALSTORAGE) ====================

// 1) Khởi tạo giỏ hàng từ localStorage
let cart = [];
try {
cart = JSON.parse(localStorage.getItem("cart")) || [];
if (!Array.isArray(cart)) cart = [];
} catch (e) {
cart = [];
}

// ==================== KHỞI TẠO KHI TẢI TRANG ====================
document.addEventListener("DOMContentLoaded", function () {
// Render giỏ nếu trang có khu vực cart-items
updateCart();
// Update số trên icon
updateCartCount();

// Gắn sự kiện cho tất cả nút "Thêm vào giỏ"
bindAddToCartButtons();
});

// ==================== MỞ/ĐÓNG GIỎ HÀNG (SIDEBAR) ====================
function openCart() {
const cartElement = document.getElementById("cart");
if (cartElement) cartElement.style.right = "0";
}

function closeCart() {
const cartElement = document.getElementById("cart");
if (cartElement) cartElement.style.right = "-350px";
}

// ==================== LƯU / ĐỌC LOCALSTORAGE ====================
function saveCart() {
localStorage.setItem("cart", JSON.stringify(cart));
}

// ==================== THÊM VÀO GIỎ ====================
function addToCart(product) {
if (!product || !product.id) {
console.error("addToCart: thiếu product.id", product);
showToast("Không thêm được: thiếu mã sản phẩm!");
return;
}

// Chuẩn hoá dữ liệu
const cleanProduct = {
id: String(product.id),
name: (product.name || "Sản phẩm").trim(),
price: Number(product.price) || 0,
image: product.image || "/anh/default.png",
quantity: 1,
};

const existing = cart.find((it) => String(it.id) === cleanProduct.id);

if (existing) {
existing.quantity = (Number(existing.quantity) || 1) + 1;
} else {
cart.push(cleanProduct);
}

saveCart();
updateCart();
updateCartCount();
showToast("✓ Đã thêm sản phẩm vào giỏ hàng!");
}

// ==================== XOÁ SẢN PHẨM ====================
function removeFromCart(productId) {
cart = cart.filter((it) => String(it.id) !== String(productId));
saveCart();
updateCart();
updateCartCount();
showToast("Đã xóa sản phẩm khỏi giỏ hàng!");
}

// ==================== GIẢM SỐ LƯỢNG ====================
function decreaseQuantity(productId) {
const p = cart.find((it) => String(it.id) === String(productId));
if (!p) return;

const q = Number(p.quantity) || 1;
if (q > 1) {
p.quantity = q - 1;
} else {
removeFromCart(productId);
return;
}

saveCart();
updateCart();
updateCartCount();
}

// ==================== TĂNG SỐ LƯỢNG ====================
function increaseQuantity(productId) {
const p = cart.find((it) => String(it.id) === String(productId));
if (!p) return;

const q = Number(p.quantity) || 1;
p.quantity = q + 1;

saveCart();
updateCart();
updateCartCount();
}

// ==================== CẬP NHẬT GIỎ (RENDER LIST) ====================
function updateCart() {
const cartItems = document.getElementById("cart-items");
const emptyMsg = document.getElementById("empty-cart-msg");
const totalElement = document.getElementById("total-amount");

// Nếu trang hiện tại không có khu vực giỏ thì bỏ qua
if (!cartItems) return;

cartItems.innerHTML = "";
let totalAmount = 0;

if (!cart || cart.length === 0) {
if (emptyMsg) emptyMsg.style.display = "block";
if (totalElement) totalElement.innerText = "0";
return;
}

if (emptyMsg) emptyMsg.style.display = "none";

cart.forEach((sp) => {
const price = Number(sp.price) || 0;
const qty = Number(sp.quantity) || 1;
const itemTotal = price * qty;
totalAmount += itemTotal;

cartItems.innerHTML += `
    <li class="cart-item">
    <div class="cart-item-info">
        <img src="${sp.image || "/anh/default.png"}" alt="${escapeHtml(
    sp.name || ""
)}" class="cart-item-img">
        <div class="cart-item-details">
        <h4>${escapeHtml(sp.name || "Sản phẩm")}</h4>
        <p class="cart-item-price">${formatPrice(price)} ₫</p>
        </div>
    </div>

    <div class="cart-item-actions">
        <div class="quantity-controls">
        <button type="button" onclick="decreaseQuantity('${sp.id}')" class="qty-btn">-</button>
        <span class="quantity">${qty}</span>
        <button type="button" onclick="increaseQuantity('${sp.id}')" class="qty-btn">+</button>
        </div>

        <button type="button" onclick="removeFromCart('${sp.id}')" class="remove-btn" title="Xóa">
        <i class="fas fa-trash"></i>
        </button>
    </div>

    <div class="cart-item-total">
        ${formatPrice(itemTotal)} ₫
    </div>
    </li>
`;
});

if (totalElement) totalElement.innerText = formatPrice(totalAmount);
}

// ==================== CẬP NHẬT SỐ LƯỢNG ICON ====================
function updateCartCount() {
const el =
document.getElementById("cart-count") || document.querySelector(".cart-icon span");

if (!el) return;

const totalItems = cart.reduce((sum, it) => sum + (Number(it.quantity) || 1), 0);
el.innerText = totalItems;
}

// ==================== CLEAR CART ====================
function clearCart() {
if (!confirm("Bạn có chắc muốn xóa toàn bộ giỏ hàng?")) return;

cart = [];
localStorage.removeItem("cart");
updateCart();
updateCartCount();
showToast("Đã xóa toàn bộ giỏ hàng!");
}

// ==================== BIND NÚT ADD-TO-CART ====================
function bindAddToCartButtons() {
document.querySelectorAll(".add-to-cart").forEach((btn) => {
// Tránh gắn trùng
if (btn.dataset.bound === "1") return;
btn.dataset.bound = "1";

btn.addEventListener("click", function (e) {
    e.preventDefault();

    const productItem = this.closest(".product-item");
    const id = this.dataset.productId || productItem?.dataset.productId || "";
    if (!id) {
    console.error("Thiếu data-product-id trên nút hoặc product-item");
    showToast("Không thêm được: thiếu mã sản phẩm!");
    return;
    }

    // Giá ưu tiên lấy từ data-price
    const priceFromData = Number(this.dataset.price) || 0;

    // Fallback: cố gắng parse từ <p> giá
    let priceFromText = 0;
    const priceEl = productItem?.querySelector("p");
    if (priceEl) {
    const digits = (priceEl.textContent || "").replace(/[^\d]/g, "");
    priceFromText = Number(digits) || 0;
    }

    const product = {
    id: id,
    name: productItem?.querySelector("h3")?.textContent?.trim() || "Sản phẩm",
    price: priceFromData || priceFromText || 0,
    image: productItem?.querySelector(".product-img")?.src || "/anh/default.png",
    };

    addToCart(product);

    // hiệu ứng (nếu em có CSS .added)
    this.classList.add("added");
    setTimeout(() => this.classList.remove("added"), 800);
});
});
}

// ==================== FORMAT GIÁ ====================
function formatPrice(price) {
const n = Number(price) || 0;
return n.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
}

// ==================== TOAST ====================
function showToast(message) {
let toast = document.querySelector(".toast");
if (!toast) {
toast = document.createElement("div");
toast.className = "toast";
document.body.appendChild(toast);
}

toast.textContent = message;
toast.classList.add("show");

setTimeout(() => {
toast.classList.remove("show");
}, 1500);
}

// ==================== ESCAPE HTML (TRÁNH XSS TRONG innerHTML) ====================
function escapeHtml(str) {
return String(str)
.replaceAll("&", "&amp;")
.replaceAll("<", "&lt;")
.replaceAll(">", "&gt;")
.replaceAll('"', "&quot;")
.replaceAll("'", "&#039;");
}
