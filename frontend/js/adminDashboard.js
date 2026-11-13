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

// ---------- helpers ----------

const STATUS_LIST = [
  ["PENDING", "Pending"],
  ["UNDER_REVIEW", "Under Review"],
  ["IN_PROGRESS", "In Progress"],
  ["RESOLVED", "Resolved"]
];

function statusLabel(status) {
  for (let i = 0; i < STATUS_LIST.length; i++) {
    if (STATUS_LIST[i][0] === status) {
      return STATUS_LIST[i][1];
    }
  }
  return status;
}

function statusOptions(current) {
  let options = "";
  for (let i = 0; i < STATUS_LIST.length; i++) {
    const value = STATUS_LIST[i][0];
    const text = STATUS_LIST[i][1];
    const selected = value === current ? " selected" : "";
    options += `<option value="${value}"${selected}>${text}</option>`;
  }
  return options;
}

function formatDateTime(dateStr) {
  if (!dateStr) {
    return "-";
  }
  return "Date: " + dateStr.slice(0, 10) + " Time: " + dateStr.slice(11, 16);
}

function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str ?? "";
  return div.innerHTML;
}

function photoCell(url) {
  if (url && url.startsWith("https://res.cloudinary.com/")) {
    return `<a href="photo.html?url=${encodeURIComponent(url)}">View Photo</a>`;
  }
  return "No photo";
}

// ---------- load all complaints ----------

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
      table.innerHTML = '<tr><td colspan="9" style="text-align:center;">No complaints found</td></tr>';
      return;
    }

    let rows = "";

    for (let i = 0; i < complaints.length; i++) {
      const c = complaints[i];

      rows += "<tr>";
      rows += "<td>" + escapeHtml(c.title) + "</td>";
      rows += "<td>" + escapeHtml(c.address) + "</td>";
      rows += "<td>" + escapeHtml(c.category || "-") + "</td>";
      rows += "<td>" + escapeHtml(c.userName) + "<br>" + escapeHtml(c.userEmail) + "</td>";
      rows += "<td>" + formatDateTime(c.createdAt) + "</td>";
      rows += '<td id="status-text-' + c.id + '">' + statusLabel(c.status) + "</td>";
      rows += "<td>" + escapeHtml(c.priority || "-") + "</td>";
      rows += "<td>";
      rows += '<select id="status-select-' + c.id + '">' + statusOptions(c.status) + "</select> ";
      rows += '<button class="btn-update" data-id="' + c.id + '">Update</button>';
      rows += "</td>";
      rows += "<td>" + photoCell(c.imagePath) + "</td>";
      rows += "</tr>";
    }

    table.innerHTML = rows;

  } catch (err) {
    console.error(err);
    alert("Unable to connect with server");
  }
}

// ---------- update status (one handler for all Update buttons) ----------

document.getElementById("complaintTable").addEventListener("click", async (e) => {
  const btn = e.target.closest(".btn-update");
  if (!btn) {
    return;
  }

  const id = btn.dataset.id;
  const newStatus = document.getElementById("status-select-" + id).value;

  btn.disabled = true;

  try {
    const response = await fetch(API + "/api/admin/complaints/" + id + "/status", {
      method: "PUT",
      headers: {
        "Authorization": "Bearer " + token,
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ status: newStatus })
    });

    if (response.status === 401 || response.status === 403) {
      localStorage.clear();
      window.location.href = "login.html";
      return;
    }

    if (!response.ok) {
      throw new Error("Update failed");
    }

    // show the new status in the STATUS column without reloading
    document.getElementById("status-text-" + id).textContent = statusLabel(newStatus);
    alert("Status updated");

  } catch (err) {
    console.error(err);
    alert("Could not update status");
  } finally {
    btn.disabled = false;
  }
});

loadComplaints();