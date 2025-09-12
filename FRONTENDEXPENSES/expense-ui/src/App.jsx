import React, { useEffect, useState } from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Link,
  useParams,
  useNavigate,
} from "react-router-dom";

const API_BASE = "http://localhost:8080";

function safeTotal(cat) {
  return cat?.totalAmount ?? cat?.TotalAmount ?? cat?.total ?? 0;
}

// ========================== MAIN PAGE ==========================
function MainPage() {
  const [categories, setCategories] = useState([]);
  const [totalSpendings, setTotalSpendings] = useState(0);
  const [dateRange, setDateRange] = useState({ start: "", end: "" });
  const [filteredTotal, setFilteredTotal] = useState(null);

  const [newCategory, setNewCategory] = useState("");
  const [editCategory, setEditCategory] = useState({ id: null, name: "" });

  const [newExpense, setNewExpense] = useState({
    categoryId: "",
    amount: "",
    date: "",
    description: "",
  });

  const loadCategories = () => {
    fetch(`${API_BASE}/categories`)
      .then((res) => res.json())
      .then((data) => {
        setCategories(data || []);
        const total = (data || []).reduce((sum, cat) => sum + (safeTotal(cat) || 0), 0);
        setTotalSpendings(total);
      })
      .catch((e) => console.error("loadCategories", e));
  };

  useEffect(() => {
    loadCategories();
  }, []);

  const handleSearch = () => {
    if (!dateRange.start || !dateRange.end) return alert("Choose start and end dates");
    fetch(
      `${API_BASE}/expenses/between/total?startDate=${dateRange.start}&endDate=${dateRange.end}`
    )
      .then((res) => res.json())
      .then(setFilteredTotal)
      .catch((e) => console.error(e));
  };

  const createCategory = () => {
    if (!newCategory.trim()) return alert("Enter category name");
    fetch(`${API_BASE}/categories`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name: newCategory }),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to create category");
        setNewCategory("");
        loadCategories();
      })
      .catch((e) => alert(e.message));
  };

  const updateCategory = () => {
    if (!editCategory.id) return;
    if (!editCategory.name?.trim()) return alert("Enter category name");

    fetch(`${API_BASE}/categories/${editCategory.id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ id: editCategory.id, name: editCategory.name }),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to update category");
        setEditCategory({ id: null, name: "" });
        loadCategories();
      })
      .catch((e) => alert(e.message));
  };

  const createExpense = () => {
    if (!newExpense.categoryId) return alert("Select category!");
    if (!newExpense.amount) return alert("Enter amount");
    if (!newExpense.date) return alert("Select date");

    fetch(`${API_BASE}/categories/${newExpense.categoryId}/expenses`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        amount: parseFloat(newExpense.amount),
        date: newExpense.date,
        description: newExpense.description,
      }),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to create expense");
        setNewExpense({ categoryId: "", amount: "", date: "", description: "" });
        loadCategories();
      })
      .catch((e) => alert(e.message));
  };

  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold mb-2">Expense tracker</h1>
      <h2 className="mb-4">Total spendings: {totalSpendings}$</h2>

      {/* Date search */}
      <div className="mb-6 flex items-center gap-2">
        <input
          type="date"
          className="border p-2"
          value={dateRange.start}
          onChange={(e) => setDateRange({ ...dateRange, start: e.target.value })}
        />
        <input
          type="date"
          className="border p-2"
          value={dateRange.end}
          onChange={(e) => setDateRange({ ...dateRange, end: e.target.value })}
        />
        <button onClick={handleSearch} className="bg-blue-500 text-white px-4 py-2 rounded">
          Search
        </button>
        {filteredTotal !== null && (
          <span className="ml-4">Spendings in period: {filteredTotal}$</span>
        )}
      </div>

      {/* Categories table */}
      <table className="w-full border mb-6">
        <thead>
          <tr className="bg-gray-100">
            <th className="border p-2 text-left">Category</th>
            <th className="border p-2">Expenses</th>
            <th className="border p-2">%</th>
            <th className="border p-2">Actions</th>
          </tr>
        </thead>
        <tbody>
          {categories.map((cat) => {
            const total = safeTotal(cat) || 0;
            const percentage = totalSpendings ? ((total / totalSpendings) * 100).toFixed(1) : "0.0";
            return (
              <tr key={cat.id}>
                <td className="border p-2">{cat.name}</td>
                <td className="border p-2">{total}$</td>
                <td className="border p-2">{percentage}%</td>
                <td className="border p-2 flex gap-2">
                  <Link to={`/category/${cat.id}`} className="text-blue-500 underline">View</Link>
                  <button
                    className="text-yellow-600"
                    onClick={() => setEditCategory({ id: cat.id, name: cat.name })}
                  >
                    Edit
                  </button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>

      {/* Create / Edit Category */}
      <div className="mb-4">
        <h3 className="font-bold mb-2">{editCategory.id ? "Edit Category" : "Create Category"}</h3>
        <input
          type="text"
          placeholder="Category name"
          className="border p-2 mr-2"
          value={editCategory.id ? editCategory.name : newCategory}
          onChange={(e) =>
            editCategory.id ? setEditCategory({ ...editCategory, name: e.target.value }) : setNewCategory(e.target.value)
          }
        />
        <button
          onClick={editCategory.id ? updateCategory : createCategory}
          className="bg-green-500 text-white px-4 py-2 rounded"
        >
          {editCategory.id ? "Update" : "Create"}
        </button>
        {editCategory.id && (
          <button className="ml-2 border px-3 py-2" onClick={() => setEditCategory({ id: null, name: "" })}>
            Cancel
          </button>
        )}
      </div>

      {/* Create Expense */}
      <div>
        <h3 className="font-bold mb-2">Create Expense</h3>
        <select
          className="border p-2 mr-2"
          value={newExpense.categoryId}
          onChange={(e) => setNewExpense({ ...newExpense, categoryId: e.target.value })}
        >
          <option value="">Select category</option>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>
        <input
          type="number"
          placeholder="Amount"
          className="border p-2 mr-2"
          value={newExpense.amount}
          onChange={(e) => setNewExpense({ ...newExpense, amount: e.target.value })}
        />
        <input
          type="date"
          className="border p-2 mr-2"
          value={newExpense.date}
          onChange={(e) => setNewExpense({ ...newExpense, date: e.target.value })}
        />
        <input
          type="text"
          placeholder="Description"
          className="border p-2 mr-2"
          value={newExpense.description}
          onChange={(e) => setNewExpense({ ...newExpense, description: e.target.value })}
        />
        <button onClick={createExpense} className="bg-green-500 text-white px-4 py-2 rounded">Add</button>
      </div>
    </div>
  );
}

// ========================== CATEGORY PAGE ==========================
function CategoryPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [category, setCategory] = useState(null);
  const [allTotal, setAllTotal] = useState(0); // total across all categories
  const [editExpense, setEditExpense] = useState(null);

  //маё
  const [page, setPage] = useState(0);
  const [expenses, setExpenses] = useState([]);
  const [totalPages, setTotalPages] = useState(0);

  const loadExpenses = () => {
  fetch(`${API_BASE}/categories/${id}/expenses?page=${page}&size=16`)
    .then((res) => res.json())
    .then((data) => {
      setExpenses(data.content || []);   // список расходов
      setTotalPages(data.totalPages || 0); // количество страниц
    });
};


  useEffect(() => {
    loadExpenses();
  }, [id, page]);

  const loadCategory = () => {
    // fetch category details
    fetch(`${API_BASE}/categories/${id}`)
      .then((res) => res.json())
      .then((data) => setCategory(data))
      .catch((e) => console.error(e));

    // also fetch categories to compute global total
    fetch(`${API_BASE}/categories`)
      .then((res) => res.json())
      .then((data) => {
        const total = (data || []).reduce((s, c) => s + (safeTotal(c) || 0), 0);
        setAllTotal(total);
      })
      .catch((e) => console.error(e));
  };

  useEffect(() => {
    loadCategory();
  }, [id]);

  if (!category) return <p>Loading...</p>;

  const deleteExpense = (expenseId) => {
    fetch(`${API_BASE}/categories/${id}/expenses/${expenseId}`, { method: "DELETE" })
      .then(() => loadCategory())
      .catch((e) => console.error(e));
  };

  const updateExpense = () => {
    if (!editExpense) return;

    fetch(`${API_BASE}/categories/${id}/expenses/${editExpense.id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        id: editExpense.id,
        amount: parseFloat(editExpense.amount),
        date: editExpense.date,
        description: editExpense.description,
      }),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to update expense");
        setEditExpense(null);
        loadCategory();
      })
      .catch((e) => alert(e.message));
  };

  const deleteCategory = () => {
    fetch(`${API_BASE}/categories/${id}`, { method: "DELETE" })
      .then(() => navigate("/"))
      .catch((e) => console.error(e));
  };

  const percentage = category && allTotal ? ((safeTotal(category) / allTotal) * 100).toFixed(1) : "0.0";

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold">{category.name}</h1>
      <h2 className="mb-4">Total spendings in this category: {safeTotal(category)}$ ({percentage}% of all)</h2>

      <table className="w-full border mb-4">
        <thead>
          <tr className="bg-gray-100">
            <th className="border p-2">Date</th>
            <th className="border p-2">Amount</th>
            <th className="border p-2">Description</th>
            <th className="border p-2">Actions</th>
          </tr>
        </thead>
        <tbody>
          {expenses?.map((exp) => (
            <tr key={exp.id}>
              <td className="border p-2">{exp.date}</td>
              <td className="border p-2">{exp.amount}$</td>
              <td className="border p-2">{exp.description}</td>
              <td className="border p-2 flex gap-2">
                <button className="text-yellow-600" onClick={() => setEditExpense(exp)}>Edit</button>
                <button className="text-red-500" onClick={() => deleteExpense(exp.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="flex gap-2 mt-4">
    <button
      disabled={page === 0}
      onClick={() => setPage(page - 1)}
      className="px-4 py-2 border rounded disabled:opacity-50"
    >
      Prev
    </button>
    <span>
      {totalPages > 0
        ? `Page ${page + 1} of ${totalPages}`
        : "No expenses"}
    </span>

    <button
      disabled={page + 1 >= totalPages}
      onClick={() => setPage(page + 1)}
      className="px-4 py-2 border rounded disabled:opacity-50"
    >
      Next
    </button>
  </div>


      {/* Edit expense form */}
      {editExpense && (
        <div className="mb-4">
          <h3 className="font-bold mb-2">Edit Expense</h3>
          <input type="number" className="border p-2 mr-2" value={editExpense.amount}
            onChange={(e) => setEditExpense({ ...editExpense, amount: e.target.value })} />
          <input type="date" className="border p-2 mr-2" value={editExpense.date}
            onChange={(e) => setEditExpense({ ...editExpense, date: e.target.value })} />
          <input type="text" className="border p-2 mr-2" value={editExpense.description}
            onChange={(e) => setEditExpense({ ...editExpense, description: e.target.value })} />
          <button onClick={updateExpense} className="bg-green-500 text-white px-4 py-2 rounded">Save</button>
          <button onClick={() => setEditExpense(null)} className="ml-2 border px-4 py-2">Cancel</button>
        </div>
      )}

      <div className="flex gap-4">
        <button className="border px-4 py-2" onClick={() => navigate("/")}>Back</button>
        <button className="border px-4 py-2 text-red-500" onClick={deleteCategory}>Delete Category</button>
      </div>
    </div>
  );
}

// ========================== APP ==========================
export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/category/:id" element={<CategoryPage />} />
      </Routes>
    </Router>
  );
}
