import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { loginUser } from "@/services/authService";
import { toast } from "sonner";
import { AxiosError } from "axios";

export default function LoginForm() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const navigate = useNavigate();
  const { login } = useAuth();
  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const token = await loginUser({ username, password });
      login(token);
      navigate("/");
    } catch (err) {
      const error = err as AxiosError<{ message: string }>;

      if (error.response) {
        const { status, data } = error.response;
        if (status === 404) {
          toast.error(data.message || "User not found. Please register first.");
        } else if (status === 401) {
          toast.error(data.message || "Invalid credentials. Please try again.");
        } else {
          toast.error(data.message || "Login failed. Please try again.");
        }
      } else if (error.request) {
        toast.error("No response from server. Please check your connection.");
      } else {
        toast.error("Unexpected error. Please try again.");
      }

      console.error(error);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-50 px-4">
      <Card className="w-full max-w-md shadow-md border p-6">
        <CardContent>
          <h2 className="text-2xl font-semibold text-center mb-6">
            Login to Redacta
          </h2>

          <form onSubmit={handleLogin} className="space-y-4">
            <div>
              <Label htmlFor="username">Username</Label>
              <Input
                id="username"
                placeholder="johndoe"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>

            <div>
              <Label htmlFor="password">Password</Label>
              <Input
                id="password"
                type="password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <Button type="submit" className="w-full">
              Sign In
            </Button>
          </form>

          <div className="mt-4 text-center">
            <span className="text-sm text-gray-600">
              Not yet registered?{" "}
              <button
                type="button"
                className="text-blue-600 hover:underline font-medium"
                onClick={() => navigate("/register")}
              >
                Register here
              </button>
            </span>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
