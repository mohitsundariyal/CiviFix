const API = "http://localhost:5003";

const token = localStorage.getItem("token");
if (!token) {
    window.location.href = "login.html";
}
let currentMarker = null;

const fullName = localStorage.getItem("fullName");
if (fullName) {
    document.querySelector(".user-name").textContent = fullName;
}

document.querySelector(".btn-logout").addEventListener("click", () => {
    localStorage.clear();
    window.location.href = "login.html";
});

document.getElementById("complaint-form").addEventListener("submit", async (e) => {
    e.preventDefault();

    const title = document.getElementById("title").value;
    const description = document.getElementById("detailedDescription").value;
    const address = document.getElementById("address").value;
    const image = document.getElementById("image").files[0];

    const checked = document.querySelector('input[name="category"]:checked');
    const category = checked ? checked.value : "";

    if (!category) {
        alert("Please select a category");
        return;
    }

    const formData = new FormData();
    formData.append("title", title);
    formData.append("category", category);
    formData.append("description", description);
    formData.append("address", address);
    formData.append("image", image);

    try {
        const response = await fetch(API + "/api/complaints", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token
            },
            body: formData
        });

        if (response.ok) {
            alert("Complaint sent!");
            window.location.href = "user-dashboard.html";
        } else {
            let message = "Failed to submit complaint";
            try {
                const data = await response.json();
                message = data.message || message;
            } catch (_) {}
            alert(message);
        }
    } catch (err) {
        console.error(err);
        alert("Server se connect nahi ho pa raha");
    }
});

async function getPlaceName(lat, lng) {
    const url = `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lng}`;
    const res = await fetch(url);
    const data = await res.json();
    return data.display_name; // full address
}

var map = L.map('map', { doubleClickZoom: false }).setView([30.272680, 78.000970], 14);
L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; OpenStreetMap contributors'
}).addTo(map);

// Yeh line add karke dekhein
setTimeout(() => {
    map.invalidateSize();
}, 200);

map.on("dblclick", async function (e) {
    const lat = e.latlng.lat.toFixed(6);
    const lng = e.latlng.lng.toFixed(6);

    const addressInput = document.getElementById("address");
    addressInput.value = "Address dhoondh rahe hain...";

    if (currentMarker) {
        map.removeLayer(currentMarker);
    }
    currentMarker = L.marker([lat, lng]).addTo(map);

    try {
        const placeName = await getPlaceName(lat, lng);
        addressInput.value = placeName || `${lat}, ${lng}`;
    } catch (err) {
        console.error(err);
        addressInput.value = `${lat}, ${lng}`;
    }
});