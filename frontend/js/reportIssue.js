const API = "http://localhost:5003";

const token = localStorage.getItem("token");
if (!token) {
  window.location.href = "login.html";
}

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



//leaflet
var map = L.map('map').setView([30.272680,78.000970], 18);
L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map);


map.on("dblclick",function(e){
  document.getElementById("address").value=`${e.latlng.lat},${e.latlng.lng}`;
})