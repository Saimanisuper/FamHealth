/**
 * Family Health Vault - Frontend Single Page Application Logic
 */

// Application State
const state = {
  token: localStorage.getItem('fhv_token') || null,
  user: JSON.parse(localStorage.getItem('fhv_user') || 'null'),
  patients: [],
  activePatient: null,
  vitals: [],
  medications: [],
  documents: [],
  activeTab: 'vitals',
  activeShareToken: null,
  chartInstance: null
};

// --- INITIALIZATION ---
document.addEventListener('DOMContentLoaded', () => {
  if (state.token && state.user) {
    initApp();
  } else {
    showAuthScreen(true);
  }
  lucide.createIcons();
});

// --- AUTHENTICATION ---
function showAuthScreen(show) {
  const authScreen = document.getElementById('auth-screen');
  if (show) {
    authScreen.classList.remove('hidden');
    authScreen.classList.add('flex');
  } else {
    authScreen.classList.add('hidden');
    authScreen.classList.remove('flex');
  }
}

async function quickLogin(username, password) {
  document.getElementById('username-input').value = username;
  document.getElementById('password-input').value = password;
  await handleLogin();
}

async function handleLogin(e) {
  if (e) e.preventDefault();
  const username = document.getElementById('username-input').value.trim();
  const password = document.getElementById('password-input').value;

  if (!username || !password) {
    showToast('Please enter username and password', 'warning');
    return;
  }

  const btn = document.getElementById('login-btn');
  const originalHtml = btn.innerHTML;
  btn.innerHTML = '<span>Verifying credentials...</span>';
  btn.disabled = true;

  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });

    const result = await res.json();

    if (!res.ok || !result.success) {
      throw new Error(result.message || 'Invalid credentials');
    }

    state.token = result.data.token;
    state.user = {
      username: result.data.username,
      role: 'ROLE_' + result.data.role
    };

    localStorage.setItem('fhv_token', state.token);
    localStorage.setItem('fhv_user', JSON.stringify(state.user));

    showAuthScreen(false);
    showToast(`Welcome back, ${state.user.username} (${state.user.role})!`, 'success');
    initApp();
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    btn.innerHTML = originalHtml;
    btn.disabled = false;
    lucide.createIcons();
  }
}

function logout() {
  localStorage.removeItem('fhv_token');
  localStorage.removeItem('fhv_user');
  state.token = null;
  state.user = null;
  state.activePatient = null;
  showAuthScreen(true);
  showToast('Logged out of Health Vault', 'info');
}

// --- API CLIENT WRAPPER ---
async function apiCall(endpoint, method = 'GET', body = null, isFormData = false) {
  const headers = {};
  if (state.token && !endpoint.startsWith('/api/public/')) {
    headers['Authorization'] = `Bearer ${state.token}`;
  }

  if (body && !isFormData) {
    headers['Content-Type'] = 'application/json';
  }

  const config = {
    method,
    headers,
    body: isFormData ? body : (body ? JSON.stringify(body) : null)
  };

  const response = await fetch(endpoint, config);

  if (response.status === 401) {
    logout();
    throw new Error('Session expired. Please sign in again.');
  }

  if (response.status === 403) {
    throw new Error('Access Denied: Your current role is not authorized for this action.');
  }

  const data = await response.json();
  if (!response.ok || (data.success !== undefined && !data.success)) {
    throw new Error(data.message || 'Request failed');
  }

  return data;
}

// --- APPLICATION INITIALIZATION ---
async function initApp() {
  renderUserBadge();
  applyRoleSecurityUI();
  await loadPatients();
  if (state.activePatient) {
    await loadPatientData();
  }
  switchTab(state.activeTab);
  lucide.createIcons();
}

function renderUserBadge() {
  if (!state.user) return;
  document.getElementById('header-username').textContent = state.user.username;
  document.getElementById('header-role').textContent = state.user.role;
}

function applyRoleSecurityUI() {
  const isNurse = state.user && state.user.role === 'ROLE_NURSE';
  const isAdmin = state.user && state.user.role === 'ROLE_ADMIN';

  const nurseBanner = document.getElementById('nurse-guard-banner');
  if (isNurse) {
    nurseBanner.classList.remove('hidden');
    // Hide add patient and add medication for nurse
    document.getElementById('btn-add-patient')?.classList.add('hidden');
    document.getElementById('btn-add-med')?.classList.add('hidden');
    document.getElementById('btn-upload-doc')?.setAttribute('disabled', 'true');
    document.getElementById('btn-create-share-link')?.setAttribute('disabled', 'true');
  } else {
    nurseBanner.classList.add('hidden');
    document.getElementById('btn-add-patient')?.classList.remove('hidden');
    document.getElementById('btn-add-med')?.classList.remove('hidden');
    document.getElementById('btn-upload-doc')?.removeAttribute('disabled');
    document.getElementById('btn-create-share-link')?.removeAttribute('disabled');
  }
}

// --- PATIENT MANAGEMENT ---
async function loadPatients() {
  try {
    const res = await apiCall('/api/patients');
    state.patients = res.data || [];
    if (state.patients.length > 0) {
      if (!state.activePatient || !state.patients.some(p => p.id === state.activePatient.id)) {
        state.activePatient = state.patients[0];
      }
    }
    renderPatientDropdown();
    renderActivePatientHero();
  } catch (err) {
    console.warn('Patients fetch notice:', err.message);
  }
}

function renderPatientDropdown() {
  const select = document.getElementById('patient-selector');
  if (!select) return;
  if (state.patients.length <= 1) {
    select.classList.add('hidden');
    return;
  }
  select.classList.remove('hidden');
  select.innerHTML = state.patients.map(p => `
    <option value="${p.id}" ${state.activePatient && state.activePatient.id === p.id ? 'selected' : ''}>
      ${p.name}
    </option>
  `).join('');
}

async function selectPatientById(patientId) {
  const found = state.patients.find(p => p.id === patientId);
  if (found) {
    state.activePatient = found;
    renderActivePatientHero();
    await loadPatientData();
    showToast(`Switched active profile to ${found.name}`, 'info');
  }
}

function renderActivePatientHero() {
  if (!state.activePatient) return;
  document.getElementById('patient-name-display').textContent = state.activePatient.name;
  document.getElementById('patient-blood-badge').textContent = `Blood: ${state.activePatient.bloodGroup || 'Unknown'}`;
  document.getElementById('patient-age-badge').textContent = `${state.activePatient.age} Yrs`;
  document.getElementById('patient-allergies-display').textContent = state.activePatient.allergies || 'None Reported';

  // Avatar initials
  const initials = state.activePatient.name
    .split(' ')
    .map(n => n[0])
    .join('')
    .substring(0, 2)
    .toUpperCase();
  document.getElementById('patient-avatar-initials').textContent = initials;
}

// --- TAB NAVIGATION ---
function switchTab(tab) {
  state.activeTab = tab;
  const tabs = ['vitals', 'medications', 'documents', 'share'];

  tabs.forEach(t => {
    const view = document.getElementById(`view-${t}`);
    const deskBtn = document.getElementById(`tab-btn-${t}`);
    const mobBtn = document.getElementById(`mob-tab-${t}`);

    if (t === tab) {
      view.classList.remove('hidden');
      deskBtn?.classList.add('tab-active');
      mobBtn?.classList.add('mobile-tab-active');
    } else {
      view.classList.add('hidden');
      deskBtn?.classList.remove('tab-active');
      mobBtn?.classList.remove('mobile-tab-active');
    }
  });

  if (tab === 'vitals') {
    loadVitals();
  } else if (tab === 'medications') {
    loadMedications();
  } else if (tab === 'documents') {
    loadDocuments();
  }

  lucide.createIcons();
}

async function loadPatientData() {
  await Promise.allSettled([
    loadVitals(),
    loadMedications(),
    loadDocuments()
  ]);
}

// --- VITALS TELEMETRY ---
async function loadVitals() {
  if (!state.activePatient || !state.activePatient.id) return;

  // Role check: Nurse cannot read historical vitals logs per requirement ("ROLE_NURSE can only POST to the VitalsLog")
  if (state.user && state.user.role === 'ROLE_NURSE') {
    document.getElementById('vitals-table-body').innerHTML = `
      <tr>
        <td colspan="5" class="py-6 text-center text-slate-400">
          <p class="font-semibold text-slate-600">Restricted Nurse View</p>
          <span class="text-xs">Clinical history is protected. Please use the form above to record today's vitals.</span>
        </td>
      </tr>
    `;
    renderMockNurseChart();
    return;
  }

  try {
    const res = await apiCall(`/api/vitals/${state.activePatient.id}`);
    state.vitals = res.data || [];
    renderVitalsTable();
    renderVitalsChart();
  } catch (err) {
    console.error('Error fetching vitals:', err);
  }
}

function renderVitalsTable() {
  const tbody = document.getElementById('vitals-table-body');
  if (state.vitals.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="5" class="py-6 text-center text-slate-400">No vitals recorded yet. Log the first reading above!</td>
      </tr>
    `;
    return;
  }

  tbody.innerHTML = state.vitals.map(v => {
    const isNormalBp = v.bloodPressureSys <= 130 && v.bloodPressureDia <= 85;
    const isNormalSugar = v.fastingSugar <= 110;
    const dateStr = new Date(v.date).toLocaleString([], { dateStyle: 'short', timeStyle: 'short' });

    return `
      <tr class="hover:bg-slate-50/80 transition">
        <td class="py-3 px-4 font-mono font-medium text-slate-700">${dateStr}</td>
        <td class="py-3 px-4">
          <span class="font-bold ${isNormalBp ? 'text-slate-800' : 'text-amber-700'}">${v.bloodPressureSys}/${v.bloodPressureDia}</span>
          <span class="text-[10px] text-slate-400"> mmHg</span>
        </td>
        <td class="py-3 px-4">
          <span class="font-bold ${isNormalSugar ? 'text-slate-800' : 'text-rose-700'}">${v.fastingSugar}</span>
          <span class="text-[10px] text-slate-400"> mg/dL</span>
        </td>
        <td class="py-3 px-4 max-w-xs truncate text-slate-600">${v.notes || '-'}</td>
        <td class="py-3 px-4">
          <span class="px-2 py-0.5 rounded-full text-[10px] font-bold ${isNormalBp && isNormalSugar ? 'bg-emerald-100 text-emerald-800' : 'bg-amber-100 text-amber-800'}">
            ${isNormalBp && isNormalSugar ? 'Optimal' : 'Elevated'}
          </span>
        </td>
      </tr>
    `;
  }).join('');
}

function renderVitalsChart() {
  const ctx = document.getElementById('vitalsChart').getContext('2d');
  if (state.chartInstance) {
    state.chartInstance.destroy();
  }

  // Reverse to display chronologically from left to right
  const sorted = [...state.vitals].reverse();
  const labels = sorted.map(v => new Date(v.date).toLocaleDateString([], { month: 'short', day: 'numeric' }));
  const sysData = sorted.map(v => v.bloodPressureSys);
  const diaData = sorted.map(v => v.bloodPressureDia);
  const sugarData = sorted.map(v => v.fastingSugar);

  state.chartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: labels.length ? labels : ['Day 1', 'Day 2', 'Day 3', 'Today'],
      datasets: [
        {
          label: 'Systolic BP',
          data: sysData.length ? sysData : [130, 128, 122, 120],
          borderColor: '#0284c7',
          backgroundColor: 'rgba(2, 132, 199, 0.05)',
          tension: 0.3,
          borderWidth: 2.5,
          pointRadius: 4,
          pointBackgroundColor: '#0284c7'
        },
        {
          label: 'Diastolic BP',
          data: diaData.length ? diaData : [85, 82, 80, 78],
          borderColor: '#94a3b8',
          borderDash: [4, 4],
          tension: 0.3,
          borderWidth: 2,
          pointRadius: 3,
          pointBackgroundColor: '#94a3b8'
        },
        {
          label: 'Blood Sugar (mg/dL)',
          data: sugarData.length ? sugarData : [105, 102.5, 98, 96],
          borderColor: '#e11d48',
          backgroundColor: 'rgba(225, 29, 72, 0.05)',
          tension: 0.3,
          borderWidth: 2,
          pointRadius: 4,
          pointBackgroundColor: '#e11d48'
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: { mode: 'index', intersect: false },
      plugins: {
        legend: { display: false }
      },
      scales: {
        y: {
          min: 60,
          max: 160,
          grid: { color: '#f1f5f9' },
          ticks: { font: { size: 10 } }
        },
        x: {
          grid: { display: false },
          ticks: { font: { size: 10 } }
        }
      }
    }
  });
}

function renderMockNurseChart() {
  const ctx = document.getElementById('vitalsChart').getContext('2d');
  if (state.chartInstance) {
    state.chartInstance.destroy();
  }
  state.chartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri'],
      datasets: [
        {
          label: 'Nurse Telemetry Range',
          data: [125, 128, 122, 120, 118],
          borderColor: '#10b981',
          tension: 0.3,
          borderWidth: 2
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } }
    }
  });
}

async function submitVitals(e) {
  e.preventDefault();
  const sys = parseInt(document.getElementById('vital-sys').value);
  const dia = parseInt(document.getElementById('vital-dia').value);
  const sugar = parseFloat(document.getElementById('vital-sugar').value);
  const notes = document.getElementById('vital-notes').value.trim();

  const payload = {
    patientId: state.activePatient?.id,
    date: new Date().toISOString(),
    bloodPressureSys: sys,
    bloodPressureDia: dia,
    fastingSugar: sugar,
    notes: notes
  };

  const btn = document.getElementById('btn-save-vitals');
  btn.disabled = true;
  btn.innerHTML = '<span>Saving...</span>';

  try {
    await apiCall('/api/vitals', 'POST', payload);
    showToast('Vitals recorded successfully!', 'success');
    document.getElementById('vitals-form').reset();
    await loadVitals();
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.innerHTML = '<i data-lucide="check" class="w-4 h-4"></i><span>Record Vitals Log</span>';
    lucide.createIcons();
  }
}

// --- MEDICATION ENGINE ---
async function loadMedications() {
  if (!state.activePatient || !state.activePatient.id) return;
  if (state.user && state.user.role === 'ROLE_NURSE') {
    document.getElementById('medications-grid').innerHTML = `
      <div class="col-span-full p-8 text-center bg-white rounded-2xl border border-slate-200 text-slate-400">
        <i data-lucide="shield-alert" class="w-8 h-8 text-amber-500 mx-auto mb-2"></i>
        <p class="font-bold text-slate-700">Access Restricted</p>
        <p class="text-xs">Medication management is restricted to family administrators and viewers.</p>
      </div>
    `;
    lucide.createIcons();
    return;
  }

  try {
    const res = await apiCall(`/api/medications/${state.activePatient.id}`);
    state.medications = res.data || [];
    renderMedications();
  } catch (err) {
    console.error('Error fetching medications:', err);
  }
}

function renderMedications() {
  const container = document.getElementById('medications-grid');
  const lowPillsBanner = document.getElementById('med-low-inventory-banner');
  const lowBadge = document.getElementById('badge-low-pills');

  let hasLowInventory = false;
  let lowMedNames = [];

  if (state.medications.length === 0) {
    container.innerHTML = `
      <div class="col-span-full p-8 text-center bg-white rounded-2xl border border-slate-200 text-slate-400">
        No active medications. Add a prescription above.
      </div>
    `;
    lowPillsBanner.classList.add('hidden');
    lowBadge.classList.add('hidden');
    return;
  }

  container.innerHTML = state.medications.map(m => {
    const isLow = m.currentPillCount < 5;
    if (isLow) {
      hasLowInventory = true;
      lowMedNames.push(`${m.medicineName} (${m.currentPillCount} pills left)`);
    }

    const timesHtml = (m.reminderTimes || []).map(t => {
      return `<span class="px-2 py-0.5 rounded-md bg-slate-100 text-slate-700 text-[10px] font-mono font-bold">${t}</span>`;
    }).join(' ');

    return `
      <div class="bg-white rounded-2xl p-5 border ${isLow ? 'border-amber-300 shadow-amber-50' : 'border-slate-200'} shadow-sm flex flex-col justify-between relative overflow-hidden">
        ${isLow ? '<div class="absolute top-0 right-0 left-0 h-1 bg-amber-500"></div>' : ''}
        <div>
          <div class="flex items-start justify-between gap-2 mb-2">
            <h4 class="font-bold text-slate-900 text-sm leading-snug">${m.medicineName}</h4>
            <span class="px-2 py-0.5 rounded-full text-[10px] font-bold ${isLow ? 'bg-amber-100 text-amber-800 border border-amber-300' : 'bg-emerald-100 text-emerald-800'}">
              ${isLow ? '⚠️ Refill Soon' : 'In Stock'}
            </span>
          </div>

          <p class="text-xs text-slate-500 font-medium mb-3">${m.dosage}</p>

          <div class="bg-slate-50 p-3 rounded-xl border border-slate-100 mb-3 space-y-2">
            <div class="flex justify-between text-xs">
              <span class="text-slate-500">Pills Remaining:</span>
              <span class="font-bold ${isLow ? 'text-amber-600 font-mono text-sm' : 'text-slate-800 font-mono'}">${m.currentPillCount}</span>
            </div>
            <div class="w-full bg-slate-200 h-1.5 rounded-full overflow-hidden">
              <div class="${isLow ? 'bg-amber-500' : 'bg-emerald-500'} h-full rounded-full" style="width: ${Math.min(100, (m.currentPillCount / 30) * 100)}%"></div>
            </div>
            <div class="flex justify-between text-[11px] text-slate-400">
              <span>Strips: ${m.totalStripCount || 1}</span>
              <span>Capacity: 30 pills/strip</span>
            </div>
          </div>
        </div>

        <div>
          <span class="block text-[11px] font-semibold text-slate-500 mb-1.5">Scheduled Daily Times:</span>
          <div class="flex flex-wrap gap-1.5">
            ${timesHtml || '<span class="text-xs text-slate-400">No reminder set</span>'}
          </div>
        </div>

        <!-- Pill Consumption & Refill Controls -->
        <div class="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between gap-2">
          <button onclick="consumePill('${m.id}')" class="flex-1 py-1.5 px-2 bg-emerald-50 hover:bg-emerald-100 border border-emerald-200 text-emerald-700 font-semibold text-xs rounded-xl transition flex items-center justify-center gap-1">
            <i data-lucide="check" class="w-3.5 h-3.5"></i>
            <span>Take Dose (-1)</span>
          </button>
          <button onclick="refillPills('${m.id}')" class="py-1.5 px-2.5 bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold text-xs rounded-xl transition flex items-center justify-center gap-1" title="Add a full strip (+30 pills)">
            <i data-lucide="refresh-cw" class="w-3.5 h-3.5"></i>
            <span>Refill (+30)</span>
          </button>
        </div>
      </div>
    `;
  }).join('');

  lucide.createIcons();

  if (hasLowInventory) {
    lowPillsBanner.classList.remove('hidden');
    document.getElementById('med-low-inventory-text').textContent =
      `Critical Inventory Alert: ${lowMedNames.join(', ')}. Please place pharmacy refill order today.`;
    lowBadge.classList.remove('hidden');
  } else {
    lowPillsBanner.classList.add('hidden');
    lowBadge.classList.add('hidden');
  }
}

async function consumePill(medId) {
  try {
    await apiCall(`/api/medications/${medId}/consume`, 'PATCH');
    showToast('Dose recorded: 1 pill consumed!', 'success');
    await loadMedications();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function refillPills(medId) {
  try {
    await apiCall(`/api/medications/${medId}/refill?pills=30`, 'PATCH');
    showToast('Prescription refilled with +30 pills (1 strip)!', 'success');
    await loadMedications();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function handleCreateMedication(e) {
  e.preventDefault();
  const name = document.getElementById('med-new-name').value.trim();
  const dosage = document.getElementById('med-new-dosage').value.trim();
  const strips = parseInt(document.getElementById('med-new-strips').value) || 1;
  const pills = parseInt(document.getElementById('med-new-pills').value);
  const time = document.getElementById('med-new-time').value;

  const payload = {
    patientId: state.activePatient.id,
    medicineName: name,
    dosage: dosage,
    totalStripCount: strips,
    currentPillCount: pills,
    reminderTimes: [time + ':00']
  };

  try {
    await apiCall('/api/medications', 'POST', payload);
    showToast('Prescription added successfully!', 'success');
    closeModal('modal-medication');
    await loadMedications();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function simulateHourlyCronJob() {
  showToast('Executing hourly scheduler job: Evaluating pill counters & schedule...', 'info');
  setTimeout(() => {
    showToast('[SMS SIMULATION DISPATCHED]: "Dear Robert, it is time for your scheduled Amlodipine (5mg)"', 'success');
  }, 1200);
}

// --- DOCUMENT VAULT (AWS S3) ---
async function loadDocuments() {
  if (!state.activePatient || !state.activePatient.id) return;
  if (state.user && state.user.role === 'ROLE_NURSE') {
    document.getElementById('documents-grid').innerHTML = `
      <div class="col-span-full p-8 text-center bg-white rounded-2xl border border-slate-200 text-slate-400">
        <i data-lucide="shield-alert" class="w-8 h-8 text-amber-500 mx-auto mb-2"></i>
        <p class="font-bold text-slate-700">Access Restricted</p>
        <p class="text-xs">Medical document vault is restricted to family administrators and viewers.</p>
      </div>
    `;
    lucide.createIcons();
    return;
  }

  try {
    const res = await apiCall(`/api/documents/patient/${state.activePatient.id}`);
    state.documents = res.data || [];
    renderDocuments();
  } catch (err) {
    console.error('Error fetching documents:', err);
  }
}

function renderDocuments() {
  const container = document.getElementById('documents-grid');
  const isAdmin = state.user && state.user.role === 'ROLE_ADMIN';

  if (state.documents.length === 0) {
    container.innerHTML = `
      <div class="col-span-full p-8 text-center bg-white rounded-2xl border border-slate-200 text-slate-400">
        No documents uploaded yet. Upload your first lab report or scan above.
      </div>
    `;
    return;
  }

  container.innerHTML = state.documents.map(d => {
    const dateStr = new Date(d.uploadDate).toLocaleDateString([], { month: 'short', day: 'numeric', year: 'numeric' });
    const tagsHtml = (d.tags || []).map(t => `<span class="px-2 py-0.5 rounded-md bg-slate-100 text-slate-600 text-[10px] font-semibold">#${t}</span>`).join(' ');

    let badgeColor = 'bg-sky-100 text-sky-800';
    if (d.documentType === 'LAB_REPORT') badgeColor = 'bg-emerald-100 text-emerald-800';
    if (d.documentType === 'SCAN') badgeColor = 'bg-indigo-100 text-indigo-800';

    return `
      <div class="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
        <div>
          <div class="flex items-start justify-between gap-2 mb-2">
            <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold ${badgeColor}">${d.documentType}</span>
            <span class="text-[11px] text-slate-400">${dateStr}</span>
          </div>
          <h4 class="font-bold text-slate-900 text-sm truncate mb-1" title="${d.fileName || 'Medical File'}">
            ${d.fileName || 'Medical Document'}
          </h4>
          <p class="text-xs text-slate-400 font-mono truncate mb-3">${d.fileUrl}</p>
          <div class="flex flex-wrap gap-1 mb-4">
            ${tagsHtml}
          </div>
        </div>

        <div class="pt-3 border-t border-slate-100 flex items-center justify-between">
          <a href="${d.fileUrl}" target="_blank" class="text-xs font-semibold text-emerald-700 hover:text-emerald-800 flex items-center gap-1">
            <i data-lucide="external-link" class="w-3.5 h-3.5"></i>
            <span>Open in S3</span>
          </a>
          ${isAdmin ? `
            <button onclick="deleteDocument('${d.id}')" class="text-xs text-rose-600 hover:text-rose-800 p-1.5 rounded-lg hover:bg-rose-50 transition" title="Delete document">
              <i data-lucide="trash-2" class="w-4 h-4"></i>
            </button>
          ` : ''}
        </div>
      </div>
    `;
  }).join('');

  lucide.createIcons();
}

async function uploadDocument(e) {
  e.preventDefault();
  const fileInput = document.getElementById('doc-file-input');
  const typeSelect = document.getElementById('doc-type-select');
  const tagsInput = document.getElementById('doc-tags-input');

  if (!fileInput.files || fileInput.files.length === 0) {
    showToast('Please select a file to upload', 'warning');
    return;
  }

  const formData = new FormData();
  formData.append('file', fileInput.files[0]);
  formData.append('patientId', state.activePatient.id);
  formData.append('documentType', typeSelect.value);

  const tags = tagsInput.value.split(',').map(t => t.trim()).filter(Boolean);
  tags.forEach(tag => formData.append('tags', tag));

  const btn = document.getElementById('btn-upload-doc');
  btn.disabled = true;
  btn.innerHTML = '<span>Uploading to S3...</span>';

  try {
    await apiCall('/api/documents/upload', 'POST', formData, true);
    showToast('Document uploaded successfully to S3!', 'success');
    document.getElementById('document-upload-form').reset();
    await loadDocuments();
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.innerHTML = '<i data-lucide="cloud" class="w-3.5 h-3.5"></i><span>Upload S3</span>';
    lucide.createIcons();
  }
}

async function deleteDocument(docId) {
  if (!confirm('Are you sure you want to permanently delete this medical document from AWS S3 and database?')) {
    return;
  }

  try {
    await apiCall(`/api/documents/${docId}`, 'DELETE');
    showToast('Document deleted successfully', 'success');
    await loadDocuments();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// --- 24-HOUR EMERGENCY SHARING ---
async function createShareLink() {
  if (!state.activePatient || !state.activePatient.id) {
    showToast('Please select a patient first', 'warning');
    return;
  }

  const btn = document.getElementById('btn-create-share-link');
  btn.disabled = true;
  btn.innerHTML = '<span>Generating Cryptographic Token...</span>';

  try {
    const res = await apiCall(`/api/share/${state.activePatient.id}`, 'POST');
    state.activeShareToken = res.data.uuidToken;

    const fullUrl = `${window.location.origin}${res.data.shareUrl}`;
    document.getElementById('share-url-input').value = fullUrl;
    document.getElementById('share-link-result').classList.remove('hidden');
    showToast('24-Hour emergency share link generated!', 'success');
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.innerHTML = '<i data-lucide="link" class="w-4 h-4"></i><span>Generate 24-Hour Link</span>';
    lucide.createIcons();
  }
}

function quickShareActivePatient() {
  switchTab('share');
  createShareLink();
}

function copyShareUrl() {
  const input = document.getElementById('share-url-input');
  input.select();
  navigator.clipboard.writeText(input.value);
  showToast('Share link copied to clipboard!', 'info');
}

async function testPublicLinkModal() {
  if (!state.activeShareToken) return;
  const modal = document.getElementById('modal-doctor-preview');

  try {
    // Explicitly call public endpoint without Authorization header to test zero-auth
    const response = await fetch(`/api/public/share/${state.activeShareToken}`);
    const data = await response.json();

    if (!response.ok || !data.success) {
      throw new Error(data.message || 'Shared link expired or invalid');
    }

    const patient = data.data;
    document.getElementById('doctor-patient-info').innerHTML = `
      <div class="bg-slate-50 p-4 rounded-xl border border-slate-200">
        <h3 class="text-sm font-bold text-slate-900">${patient.patientName}</h3>
        <p class="text-xs text-slate-500 mt-1">Age: <strong>${patient.age}</strong> | Blood Group: <strong>${patient.bloodGroup || 'N/A'}</strong></p>
        <div class="mt-2 text-xs font-semibold text-rose-700 bg-rose-50 px-2 py-1 rounded border border-rose-200">
          ⚠️ Critical Allergies: ${patient.allergies || 'None'}
        </div>
      </div>
    `;

    const docsContainer = document.getElementById('doctor-doc-list');
    if (!patient.documents || patient.documents.length === 0) {
      docsContainer.innerHTML = '<p class="text-xs text-slate-400">No medical records uploaded for this patient.</p>';
    } else {
      docsContainer.innerHTML = patient.documents.map(d => `
        <div class="p-3 bg-white border border-slate-200 rounded-xl flex items-center justify-between text-xs">
          <div>
            <span class="font-bold text-slate-800">${d.fileName || 'Record'}</span>
            <span class="text-[10px] text-slate-400 block">${d.documentType}</span>
          </div>
          <a href="${d.fileUrl}" target="_blank" class="px-2.5 py-1 bg-emerald-600 text-white rounded-lg font-semibold hover:bg-emerald-700">View</a>
        </div>
      `).join('');
    }

    modal.classList.remove('hidden');
    modal.classList.add('flex');
    lucide.createIcons();
  } catch (err) {
    showToast('Failed to preview link: ' + err.message, 'error');
  }
}

// --- MODALS & UTILITIES ---
function openNewPatientModal() {
  const modal = document.getElementById('modal-patient');
  modal.classList.remove('hidden');
  modal.classList.add('flex');
  lucide.createIcons();
}

function openNewMedModal() {
  const modal = document.getElementById('modal-medication');
  modal.classList.remove('hidden');
  modal.classList.add('flex');
  lucide.createIcons();
}

function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  modal.classList.add('hidden');
  modal.classList.remove('flex');
}

async function handleCreatePatient(e) {
  e.preventDefault();
  const name = document.getElementById('patient-new-name').value.trim();
  const age = parseInt(document.getElementById('patient-new-age').value);
  const blood = document.getElementById('patient-new-blood').value.trim();
  const allergies = document.getElementById('patient-new-allergies').value.trim();

  const payload = { name, age, bloodGroup: blood, allergies };

  try {
    const res = await apiCall('/api/patients', 'POST', payload);
    showToast('Patient profile created successfully!', 'success');
    closeModal('modal-patient');
    await loadPatients();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// --- TOAST NOTIFICATIONS ---
function showToast(message, type = 'info') {
  const container = document.getElementById('toast-container');
  const toast = document.createElement('div');

  let bg = 'bg-slate-900 text-white border-slate-700';
  let icon = 'info';
  if (type === 'success') {
    bg = 'bg-emerald-700 text-white border-emerald-800';
    icon = 'check-circle';
  } else if (type === 'error') {
    bg = 'bg-rose-700 text-white border-rose-800';
    icon = 'alert-triangle';
  } else if (type === 'warning') {
    bg = 'bg-amber-600 text-white border-amber-700';
    icon = 'alert-circle';
  }

  toast.className = `${bg} pointer-events-auto p-3.5 rounded-xl shadow-xl border text-xs font-semibold flex items-center gap-2.5 transition-all duration-300 transform translate-y-2 opacity-0`;
  toast.innerHTML = `<i data-lucide="${icon}" class="w-4 h-4 shrink-0"></i><span>${message}</span>`;

  container.appendChild(toast);
  lucide.createIcons();

  // Animation frame for entrance
  requestAnimationFrame(() => {
    toast.classList.remove('translate-y-2', 'opacity-0');
  });

  setTimeout(() => {
    toast.classList.add('opacity-0', '-translate-y-2');
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}
