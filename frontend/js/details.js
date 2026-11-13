const API = "http://localhost:5003";

const token = localStorage.getItem("token");

// URL se complaint ID lena
const params = new URLSearchParams(window.location.search);
const complaintId = params.get("id");

async function loadComplaintDetails() {
    try {
        const response = await fetch(`${API}/api/complaints/${complaintId}`, {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            throw new Error("Unable to fetch complaint details");
        }

        const complaint = await response.json();

        document.getElementById("id").textContent = complaint.id;
        document.getElementById("title").textContent = complaint.title;
        document.getElementById("category").textContent = complaint.category;
        document.getElementById("description").textContent = complaint.description;
        document.getElementById("address").textContent = complaint.address;
        
        const statusEl = document.getElementById("status");
        statusEl.textContent = complaint.status;
        // Optional styling class based on status text
        if(complaint.status) {
            statusEl.className = `value status-badge ${complaint.status.toLowerCase()}`;
        }

        document.getElementById("createdAt").textContent = complaint.createdAt;

        const img = document.getElementById("complaintImage");

        if (complaint.imagePath && complaint.imagePath.startsWith("http")) {
            img.src = complaint.imagePath;
            img.style.display = "block";
        } else {
            img.style.display = "none";  
        }

    } catch (error) {
        console.error(error);
        alert("Unable to load complaint details");
    }
}

// Back Button Navigation Logic
document.getElementById("btn-back").addEventListener("click", () => {
  if (document.referrer && document.referrer !== window.location.href) {
    window.location.href = document.referrer;
  } else if (window.history.length > 1) {
    window.history.back();
  } else {
    window.location.href = "admin-dashboard.html"; 
  }
});

loadComplaintDetails();