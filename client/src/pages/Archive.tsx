import React from "react";
import Navbar from "@/components/Navbar";
import DocumentArchive from "@/components/DocumentArchive";
import { Button } from "@/components/ui/button";
import { ArrowLeft } from "lucide-react";
import { useNavigate } from "react-router-dom";
const Archive = () => {
  const navigate = useNavigate();
  return <div className="min-h-screen flex flex-col">
      <Navbar />
      
      <main className="flex-1 pt-24 pb-12 px-6">
        <div className="max-w-7xl mx-auto">
          <div className="mb-6">
            <Button variant="ghost" onClick={() => navigate("/")} className="mb-4">
              <ArrowLeft className="mr-2 h-4 w-4" />
              Back to Home
            </Button>
            <h1 className="text-3xl font-bold">Document Archive</h1>
            <p className="text-muted-foreground">
              Access and manage your uploaded documents
            </p>
          </div>
          
          <DocumentArchive />
        </div>
      </main>

      
    </div>;
};
export default Archive;