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

let allComplaints = [];
let currentFilter = "ALL";
let searchQuery = "";

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
        return `<a href="photo.html?url=${encodeURIComponent(url)}" target="_blank">View Photo</a>`;
    }
    return "No photo";
}

// ---------- render table ----------

function renderTable() {
    const table = document.getElementById("complaintTable");

    // Filter by priority tab and search query
    const filtered = allComplaints.filter(c => {
        const matchesTab = (currentFilter === "ALL") || (c.priority && c.priority.toUpperCase() === currentFilter);
        
        const q = searchQuery.toLowerCase();
        const matchesSearch = !q || 
            (c.title && c.title.toLowerCase().includes(q)) || 
            (c.address && c.address.toLowerCase().includes(q)) ||
            (c.userName && c.userName.toLowerCase().includes(q)) ||
            (c.userEmail && c.userEmail.toLowerCase().includes(q));

        return matchesTab && matchesSearch;
    });

    if (filtered.length === 0) {
        table.innerHTML = '<tr><td colspan="9" style="text-align:center; padding: 20px; color: #64748b;">No complaints found</td></tr>';
        return;
    }

    let rows = "";
    for (let i = 0; i < filtered.length; i++) {
        const c = filtered[i];
        const priorityClass = c.priority ? c.priority.toLowerCase() : "";

        rows += "<tr>";
        rows += "<td>" + escapeHtml(c.title) + "</td>";
        rows += "<td>" + escapeHtml(c.address) + "</td>";
        rows += "<td>" + escapeHtml(c.category || "-") + "</td>";
        rows += "<td>" + escapeHtml(c.userName) + '<div class="subtext">' + escapeHtml(c.userEmail) + "</div></td>";
        rows += "<td>" + formatDateTime(c.createdAt) + "</td>";
        rows += '<td><span class="status-text ' + (c.status ? c.status.toLowerCase().replace('_', '-') : '') + '" id="status-text-' + c.id + '">' + statusLabel(c.status) + "</span></td>";
        rows += '<td><span class="badge ' + priorityClass + '">' + escapeHtml(c.priority || "-") + "</span></td>";
        rows += "<td>";
        rows += '<div class="action-cell">';
        rows += '<select id="status-select-' + c.id + '">' + statusOptions(c.status) + "</select> ";
        rows += '<button class="btn-update" data-id="' + c.id + '">Update</button>';
        rows += '</div>';
        rows += "</td>";
        rows += "<td>" + photoCell(c.imagePath) + "</td>";
        rows += "</tr>";
    }

    table.innerHTML = rows;
}

// ---------- load all complaints from backend ----------

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

        // Combine high, medium, low priority arrays into one
        allComplaints = [
            ...(data.HIGH || []),
            ...(data.MEDIUM || []),
            ...(data.LOW || [])
        ];

        renderTable();

    } catch (err) {
        console.error(err);
        alert("Unable to connect with server");
    }
}

// ---------- search input listener ----------

document.getElementById("searchInput").addEventListener("input", (e) => {
    searchQuery = e.target.value;
    renderTable();
});

// ---------- filter tabs listener ----------

document.querySelector(".filter-tabs").addEventListener("click", (e) => {
    const tab = e.target.closest(".tab");
    if (!tab) return;

    document.querySelectorAll(".filter-tabs .tab").forEach(t => t.classList.remove("active"));
    tab.classList.add("active");

    currentFilter = tab.dataset.filter;
    renderTable();
});

// ---------- update status listener ----------

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

        // Update local array data as well
        const item = allComplaints.find(c => String(c.id) === String(id));
        if (item) {
            item.status = newStatus;
        }

        renderTable();
        alert("Status updated successfully");

    } catch (err) {
        console.error(err);
        alert("Could not update status");
    } finally {
        btn.disabled = false;
    }
});

loadComplaints();