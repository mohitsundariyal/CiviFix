const API = "http://localhost:5003";

const token = localStorage.getItem("token");
const role = localStorage.getItem("role");

if (!token) {
  window.location.href = "login.html";
} else if (role !== "ADMIN") {
  window.location.href = "user-dashboard.html";
}

const fullName = localStorage.getItem("fullName");
if (fullName) {
  document.querySelector(".user-profile span").textContent = fullName;
}

document.querySelector(".btn-logout").addEventListener("click", () => {
  localStorage.clear();
  window.location.href = "login.html";
});

function formatDateTime(dateStr) {
  return "Date: " + dateStr.slice(0, 10) + " Time: " + dateStr.slice(11, 16);
}

async function loadComplaints() {
  try {
    const response = await fetch(API + "/api/admin/complaints", {
      method: "GET",
      headers: { "Authorization": "Bearer " + token }
    });

    if (response.status === 401 || response.status === 403) {
      localStorage.clear();
      window.location.href = "login.html";
      return;
    }

    if (!response.ok) {
      alert("Admin response failed");
      return;
    }

    const data = await response.json();
    const table = document.getElementById("complaintTable");

    const complaints = [
      ...(data.HIGH || []),
      ...(data.MEDIUM || []),
      ...(data.LOW || [])
    ];

    if (complaints.length === 0) {
      table.innerHTML = '<tr><td colspan="8" style="text-align:center;">No complaints found</td></tr>';
      return;
    }

    let rows = "";

    for (let i = 0; i < complaints.length; i++) {
      const c = complaints[i];

      rows += "<tr>";
      rows += "<td>" + c.title + "</td>";
      rows += "<td>" + c.address + "</td>";
      rows += "<td>" + (c.category || "-") + "</td>";
      rows += "<td>" + c.userName + "<br>" + c.userEmail + "</td>";
      rows += "<td>" + formatDateTime(c.createdAt) + "</td>";
      rows += "<td>" + c.status + "</td>";
      rows += "<td>" + (c.priority || "-") + "</td>";
      rows += "<td><button>Update</button> <a href='ticket-details.html?id=" + c.id + "'>View</a></td>";
      rows += "</tr>";
    }

    table.innerHTML = rows;

  } catch (err) {
    console.error(err);
    alert("Unable to connect with server");
  }
}

loadComplaints();