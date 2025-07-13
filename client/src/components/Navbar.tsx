import React from "react";
import { Link, NavLink } from "react-router-dom";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
} from "@/components/ui/dropdown-menu";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { useAuth } from "@/contexts/AuthContext";
import { LogOut, ChevronDown } from "lucide-react";

const navItems = [
  { label: "Home", to: "/home" },
  { label: "Archive", to: "/archive" },
];

const Navbar: React.FC = () => {
  const { user, logout } = useAuth();

  const getInitial = (username: string) => {
    return username.slice(0, 1).toUpperCase();
  };

  return (
    <header className="glass-panel fixed inset-x-0 top-0 z-50">
      <div className="max-w-7xl mx-auto flex items-center justify-between h-16 px-4">
        <Link to="/home" className="text-2xl font-bold">
          Redacta
        </Link>

        {user && (
          <div className="hidden md:flex items-center space-x-6">
            {navItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  `text-sm font-medium transition-colors ${
                    isActive
                      ? "text-primary"
                      : "text-muted-foreground hover:text-primary"
                  }`
                }
              >
                {item.label}
              </NavLink>
            ))}

            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="flex items-center space-x-1">
                  <Avatar className="h-8 w-8">
                    <AvatarFallback>{getInitial(user?.username || 'U')}</AvatarFallback>
                  </Avatar>
                  <ChevronDown className="w-4 h-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuItem
                  onClick={logout}
                  className="flex items-center space-x-2 text-red-600"
                >
                  <LogOut className="w-4 h-4" />
                  <span>Logout</span>
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        )}
      </div>
    </header>
  );
};

export default Navbar;