document.addEventListener("DOMContentLoaded", () => {
    // 1. Auto-dismiss flash alerts
    const alerts = document.querySelectorAll(".alert");
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = "opacity 0.5s ease";
            alert.style.opacity = "0";
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    // 2. Live password match check on Register & Profile pages
    const newPass = document.getElementById("newPassword");
    const confirmPass = document.getElementById("confirmPassword");
    const matchHint = document.getElementById("passwordMatchHint");

    if (newPass && confirmPass && matchHint) {
        const validateMatch = () => {
            if (!confirmPass.value) {
                matchHint.textContent = "";
                return;
            }
            if (newPass.value === confirmPass.value) {
                matchHint.textContent = "Passwords match";
                matchHint.style.color = "#16a34a";
            } else {
                matchHint.textContent = "Passwords do not match";
                matchHint.style.color = "#dc2626";
            }
        };
        newPass.addEventListener("input", validateMatch);
        confirmPass.addEventListener("input", validateMatch);
    }

    // 3. Admin Dashboard Tab Switcher
    const tabButtons = document.querySelectorAll("[data-tab-target]");
    const tabPanels = document.querySelectorAll("[data-tab-panel]");
    if (tabButtons.length > 0) {
        tabButtons.forEach(btn => {
            btn.addEventListener("click", () => {
                const target = btn.getAttribute("data-tab-target");
                tabButtons.forEach(b => b.classList.remove("active"));
                btn.classList.add("active");
                tabPanels.forEach(panel => {
                    panel.style.display = panel.getAttribute("data-tab-panel") === target ? "block" : "none";
                });
            });
        });
    }

    // 4. Checkout Payment Method Input Toggle (COD vs UPI vs Card)
    const paymentRadios = document.querySelectorAll("input[name='paymentMethod']");
    const paymentRefBox = document.getElementById("paymentReferenceBox");
    const paymentRefLabel = document.getElementById("paymentReferenceLabel");
    const paymentRefInput = document.getElementById("paymentReference");

    if (paymentRadios.length > 0 && paymentRefBox) {
        const updatePaymentUI = () => {
            const selected = document.querySelector("input[name='paymentMethod']:checked").value;
            if (selected === "Cash on Delivery") {
                paymentRefBox.style.display = "none";
                paymentRefInput.required = false;
                paymentRefInput.value = "";
            } else if (selected === "UPI") {
                paymentRefBox.style.display = "block";
                paymentRefLabel.textContent = "Enter UPI ID (e.g., name@okaxis) *";
                paymentRefInput.placeholder = "yourname@upi";
                paymentRefInput.required = true;
            } else if (selected === "Card") {
                paymentRefBox.style.display = "block";
                paymentRefLabel.textContent = "Enter 16-Digit Card Number *";
                paymentRefInput.placeholder = "4532 0151 1283 0366";
                paymentRefInput.required = true;
            }
        };
        paymentRadios.forEach(r => r.addEventListener("change", updatePaymentUI));
        updatePaymentUI();
    }

    // 5. Product Details: Sync quantity selector with Buy Now button link
    const qtyInput = document.getElementById("quantity");
    const buyNowLink = document.getElementById("buyNowLink");
    if (qtyInput && buyNowLink) {
        const baseHref = buyNowLink.getAttribute("data-base-href");
        const syncBuyNowUrl = () => {
            const qty = Math.max(1, parseInt(qtyInput.value || "1", 10));
            buyNowLink.setAttribute("href", baseHref + "&quantity=" + qty);
        };
        qtyInput.addEventListener("input", syncBuyNowUrl);
        syncBuyNowUrl();
    }
});
