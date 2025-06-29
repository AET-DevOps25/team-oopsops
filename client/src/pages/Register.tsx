import { registerUser } from "@/services/authService";
import { useState, FormEvent } from "react";
import { toast } from "sonner";
import { useNavigate } from "react-router-dom";

type RegistrationData = {
  username: string;
  email: string;
  password: string;
};

export default function Register() {
  const [form, setForm] = useState<RegistrationData>({
    username: "",
    email: "",
    password: "",
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    try {
        const response = await registerUser(form);
        console.log("res: ", response);
        toast.success("Registration successful! Redirecting...");
    } catch (err) {
      console.error(err);
      toast.error("Network error");
    }
  };

  return (
    <div className="flex flex-col items-center justify-center p-8">
      <h1 className="text-xl font-bold mb-4">Register</h1>
      <form onSubmit={handleSubmit} className="flex flex-col gap-2 w-64">
        <input
          type="text"
          name="username"
          placeholder="Username"
          className="border p-2"
          value={form.username}
          onChange={handleChange}
        />
        <input
          type="email"
          name="email"
          placeholder="Email"
          className="border p-2"
          value={form.email}
          onChange={handleChange}
        />
        <input
          type="password"
          name="password"
          placeholder="Password"
          className="border p-2"
          value={form.password}
          onChange={handleChange}
        />
        <button
          type="submit"
          className="bg-blue-600 text-white rounded p-2 hover:bg-blue-700"
        >
          Register
        </button>
      </form>
    </div>
  );
}
