import { registerUser } from "@/services/authService";
import { useState, FormEvent } from "react";
import { toast } from "sonner";
import { useNavigate } from "react-router-dom";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { useAuth } from "@/contexts/AuthContext";
import { loginUser } from "@/services/authService";
import { RegistrationData } from "@/types/auth";
import { AxiosError } from "axios";

export default function Register() {
  const [form, setForm] = useState<RegistrationData>({
    username: "",
    email: "",
    password: "",
  });

  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const { login } = useAuth();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    try {
      await registerUser(form);

      const token = await loginUser({
        username: form.username,
        password: form.password,
      });
      login(token);

      toast.success("Registration successful! Redirecting...");
      setTimeout(() => {
        navigate("/");
      }, 1000);
    } catch (err) {
      const error = err as AxiosError<{ message: string }>;

      if (error.response) {
        const { status, data } = error.response;
        if (status === 409) {
          toast.error(
            data.message || "This account already exists. Please log in."
          );
        } else if (status === 400) {
          toast.error(data.message || "Invalid input. Please check your info.");
        } else {
          toast.error(data.message || "Registration failed. Please try again.");
        }
      } else if (error.request) {
        toast.error("No response from the server. Please try again later.");
      } else {
        toast.error("Unexpected error. Please contact support.");
      }

      console.error(error);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-white px-4">
      <Card className="w-full max-w-md shadow-md border p-6">
        <CardContent>
          <h2 className="text-2xl font-bold text-center mb-4">
            Create your Redacta account
          </h2>
          <p className="text-muted-foreground text-center mb-6">
            Join to securely anonymize and summarize your documents.
          </p>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <Label htmlFor="username">Username</Label>
              <Input
                id="username"
                name="username"
                value={form.username}
                onChange={handleChange}
                placeholder="johndoe"
                required
              />
            </div>

            <div>
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                placeholder="johndoe@example.com"
                required
              />
            </div>

            <div>
              <Label htmlFor="password">Password</Label>
              <Input
                id="password"
                type="password"
                name="password"
                value={form.password}
                onChange={handleChange}
                placeholder="••••••••"
                required
              />
            </div>

            <Button type="submit" className="w-full">
              Register
            </Button>
          </form>

          <div className="mt-4 text-center">
            <span className="text-sm text-gray-600">
              Already have an account?{" "}
              <button
                type="button"
                className="text-blue-600 hover:underline font-medium"
                onClick={() => navigate("/login")}
              >
                Login here
              </button>
            </span>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
