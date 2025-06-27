import React, { useState, useRef } from "react";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import Navbar from "@/components/Navbar";
import { Upload, FileText, X } from "lucide-react";
import { uploadDocument } from "@/services/documentService";
import { toast } from "sonner";

const Index = () => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [isDragOver, setIsDragOver] = useState(false);
  const [isUploading, setIsUploading] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileSelect = (file: File) => {
    const maxSizeInBytes = 20 * 1024 * 1024;
    
    if (file.type !== "application/pdf") {
      toast.error("Please select a PDF file only.");
      return;
    }
    
    if (file.size > maxSizeInBytes) {
      toast.error("File size must be less than 20MB. Please select a smaller file.");
      return;
    }
    
    setSelectedFile(file);
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      handleFileSelect(file);
    }
  };

  const handleDragOver = (event: React.DragEvent) => {
    event.preventDefault();
    setIsDragOver(true);
  };

  const handleDragLeave = (event: React.DragEvent) => {
    event.preventDefault();
    setIsDragOver(false);
  };

  const handleDrop = (event: React.DragEvent) => {
    event.preventDefault();
    setIsDragOver(false);
    const file = event.dataTransfer.files[0];
    if (file) {
      handleFileSelect(file);
    }
  };

  const handleUpload = async () => {
    if (!selectedFile) return;

    setIsUploading(true);
    
    try {
      const response = await uploadDocument(selectedFile)

      if (response.status == "PROCESSED") {
        toast.success("File uploaded successfully!");
        setSelectedFile(null);
        if (fileInputRef.current) {
          fileInputRef.current.value = "";
        }
      } else {
        toast.error("Upload failed. Please try again.");
      }
    } catch (error) {
      console.error("Upload error:", error);
      toast.error("Upload failed. Please try again.");
    } finally {
      setIsUploading(false);
    }
  };

  const removeFile = () => {
    setSelectedFile(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      
      <main className="flex-1 pt-24 pb-12 px-6">
        <div className="max-w-7xl mx-auto text-center">
          <h1 className="text-4xl font-bold mb-4">Welcome to Redacta</h1>
          <p className="text-xl text-muted-foreground max-w-2xl mx-auto mb-8">
            Securely anonymize and summarize your documents with advanced AI technology
          </p>
          
          <div className="glass-panel p-8 mb-8">
            <h2 className="text-2xl font-semibold mb-4">Upload a Document</h2>
            <p className="mb-6 text-muted-foreground">
              Select a PDF file to upload for anonymization or summarization
            </p>
            
            <div 
              className={`border-2 border-dashed rounded-lg p-8 transition-colors ${
                isDragOver 
                  ? "border-primary bg-primary/5" 
                  : "border-muted-foreground/25 hover:border-primary/50"
              }`}
              onDragOver={handleDragOver}
              onDragLeave={handleDragLeave}
              onDrop={handleDrop}
            >
              {selectedFile ? (
                <div className="flex items-center justify-center space-x-4">
                  <FileText className="w-8 h-8 text-primary" />
                  <div className="flex-1 text-left">
                    <p className="font-medium">{selectedFile.name}</p>
                    <p className="text-sm text-muted-foreground">
                      {(selectedFile.size / 1024 / 1024).toFixed(2)} MB
                    </p>
                  </div>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={removeFile}
                    disabled={isUploading}
                  >
                    <X className="w-4 h-4" />
                  </Button>
                </div>
              ) : (
                <div className="text-center">
                  <Upload className="w-12 h-12 mx-auto mb-4 text-muted-foreground" />
                  <p className="mb-2">Drag and drop your PDF file here, or</p>
                  <Button
                    variant="outline"
                    onClick={() => fileInputRef.current?.click()}
                  >
                    Choose File
                  </Button>
                </div>
              )}
              
              <input
                ref={fileInputRef}
                type="file"
                accept=".pdf"
                onChange={handleFileChange}
                className="hidden"
              />
            </div>
            
            {selectedFile && (
              <div className="mt-6">
                <Button 
                  size="lg" 
                  onClick={handleUpload}
                  disabled={isUploading}
                  className="w-full sm:w-auto"
                >
                  {isUploading ? "Uploading..." : "Upload Document"}
                </Button>
              </div>
            )}
            
            <p className="text-sm text-muted-foreground mt-4">
              Supported format: PDF (Max size: 20MB)
            </p>
          </div>
          
          <Link to="/archive">
            <Button variant="outline">View Document Archive</Button>
          </Link>
        </div>
      </main>
    </div>
  );
};

export default Index;
