
import React from "react";
import { Link } from "react-router-dom";

const Navbar = () => {
  return (
    <header className="glass-panel h-16 fixed top-0 left-0 right-0 z-50">
      <div className="max-w-7xl mx-auto h-full flex items-center justify-between px-6">
        <Link to="/" className="font-bold text-xl">Redacta</Link>
        
        <nav className="flex items-center space-x-6">
          <Link to="/" className="text-sm font-medium transition-colors hover:text-primary">
            Home
          </Link>
          <Link to="/archive" className="text-sm font-medium transition-colors hover:text-primary">
            Archive
          </Link>
        </nav>
      </div>
    </header>
  );
};

export default Navbar;
