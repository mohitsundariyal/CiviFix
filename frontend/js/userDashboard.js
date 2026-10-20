const API = "http://localhost:5003";

const token = localStorage.getItem("token");

if (!token) {
  window.location.href = "login.html";
}


const fullName = localStorage.getItem("fullName");
if (fullName) {
  document.querySelector(".user-name").textContent = fullName;
}

document.getElementById("btn-logout").addEventListener("click", () => {
  localStorage.clear();
  window.location.href = "login.html";
});

function getStatusInfo(status) {
  switch (status) {
    case "PENDING":
      return { text: "Pending", cls: "pending" };
    case "IN_PROGRESS":
      return { text: "In Progress", cls: "in-progress" };
    case "RESOLVED":
      return { text: "Resolved", cls: "resolved" };
    default:
      return { text: status, cls: "" };
  }
}

//date formate
function newformateDate(dateStr){
  return "Date:"+dateStr.slice(0,10)+ " Time:" + dateStr.slice(12,16);    
}

function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str ?? "";
  return div.innerHTML;
}

function renderStats(complaints) {
  const total = complaints.length;
  const resolved = complaints.filter(c => c.status === "RESOLVED").length;
  const inProgress = complaints.filter(
    c => c.status === "PROGRESS" || c.status === "PENDING"
  ).length;

  document.getElementById("total-complaint").textContent=total;
  document.getElementById("in-progress").textContent=inProgress;
  document.getElementById("resolved").textContent=resolved;

}

function renderTable(complaints) {
  const tbody = document.querySelector(".tickets-table tbody");

  if (complaints.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="4" style="text-align:center;">No complaints yet</td>
      </tr>`;
    return;
  }

  tbody.innerHTML = complaints.map(c => {
    const status = getStatusInfo(c.status);
    return `
      <tr>
        <td><strong>${escapeHtml(c.title)}</strong></td>
        <td><span class="dept-text">${escapeHtml(c.address)}</span></td>
        <td>${newformateDate(c.createdAt)}</td>
        <td><span class="status-text ${status.cls}">${status.text}</span></td>
      </tr>`;
  }).join("");
}

async function loadDashboard() {
  try {
    const response = await fetch(API + "/api/dashboard", {
      method: "GET",
      headers: {
        "Authorization": "Bearer " + token,
        "Content-Type": "application/json"
      }
    });

    // Token expire 
    if (response.status === 401 || response.status === 403) {
      localStorage.clear();
      window.location.href = "login.html";
      return;
    }

    if (!response.ok) {
      throw new Error("Failed to load complaints");
    }

    const complaints = await response.json();
    renderStats(complaints);
    renderTable(complaints);
  } catch (err) {
    console.error(err);
    alert("unable to load Complaints");
  }
}

loadDashboard();