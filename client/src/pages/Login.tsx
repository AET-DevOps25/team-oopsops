import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { loginUser } from "@/services/authService";
import { toast } from "sonner";

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
      toast.error("Invalid credentials. Please try again.");
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
        </CardContent>
      </Card>
    </div>
  );
}
