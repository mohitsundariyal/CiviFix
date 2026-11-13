
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
        document.getElementById("status").textContent = complaint.status;
        document.getElementById("createdAt").textContent = complaint.createdAt;

const img = document.getElementById("complaintImage");

if (complaint.imagePath && complaint.imagePath.startsWith("http")) {
    img.src = complaint.imagePath;
} else {
    img.style.display = "none";  
}

    } catch (error) {
        console.error(error);
        alert("Unable to load complaint details");
    }
}

loadComplaintDetails();
