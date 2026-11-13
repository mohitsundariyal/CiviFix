const params = new URLSearchParams(window.location.search);
const url = params.get("url");

const img = document.getElementById("proof-image");
const message = document.getElementById("photo-message");

// only show images that come from our Cloudinary account domain
if (url && url.startsWith("https://res.cloudinary.com/")) {
  img.src = url;
  img.hidden = false;
} else {
  message.hidden = false;
}

document.getElementById("btn-back").addEventListener("click", () => {
  if (window.history.length > 1) {
    window.history.back();
  } else {
    window.location.href = "admin-dashboard.html"; 
  }
});