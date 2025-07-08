import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/AuthContext";

const Navbar = () => {
  const { isAuthenticated, user, logout } = useAuth();

  return (
    <header className="glass-panel h-16 fixed top-0 left-0 right-0 z-50">
      <div className="max-w-7xl mx-auto h-full flex items-center justify-between px-6">
        <Link to="/" className="font-bold text-xl">Redacta</Link>

        <nav className="flex items-center space-x-4">
          <Link to="/" className="text-sm font-medium transition-colors hover:text-primary">
            Home
          </Link>
          <Link to="/archive" className="text-sm font-medium transition-colors hover:text-primary">
            Archive
          </Link>

          {isAuthenticated ? (
            <>
              <span className="text-sm text-muted-foreground">{user?.username}</span>
              <Button variant="outline" onClick={logout} className="text-sm">Logout</Button>
            </>
          ) : (
            <Link to="/login">
              <Button variant="default" size="sm">Login</Button>
            </Link>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Navbar;
