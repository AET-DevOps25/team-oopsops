import { registerUser } from "@/services/authService";
import { useState, FormEvent } from "react";
import { toast } from "sonner";
import { useNavigate } from "react-router-dom";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Label } from "@/components/ui/label";

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

  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    try {
        await registerUser(form);
        toast.success("Registration successful! Redirecting...");
        setTimeout(() => {
          navigate("/");
        }, 1000);
    } catch (err) {
      console.error(err);
      toast.error("Network error");
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-white px-4">
      <Card className="w-full max-w-md shadow-md border p-6">
        <CardContent>
          <h2 className="text-2xl font-bold text-center mb-4">Create your Redacta account</h2>
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
        </CardContent>
      </Card>
    </div>
  );
}
