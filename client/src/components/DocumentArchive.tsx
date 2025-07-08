
import React from "react";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";

// Mock data for demonstration
const mockDocuments = [
  {
    id: "doc1",
    name: "Financial Report 2024",
    date: "2024-05-10",
    status: "Anonymized"
  },
  {
    id: "doc2",
    name: "Employee Records",
    date: "2024-05-08",
    status: "Original"
  },
  {
    id: "doc3",
    name: "Contract Agreement",
    date: "2024-05-05",
    status: "Summarized"
  }
];

const DocumentArchive = () => {
  return (
    <div className="space-y-6">
      {mockDocuments.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {mockDocuments.map((doc) => (
            <div key={doc.id} className="glass-panel p-6 rounded-xl">
              <h3 className="font-medium text-lg mb-2">{doc.name}</h3>
              <div className="flex justify-between text-sm text-muted-foreground mb-4">
                <span>{doc.date}</span>
                <span className={`px-2 py-0.5 rounded-full ${
                  doc.status === "Anonymized" 
                    ? "bg-green-100 text-green-800" 
                    : doc.status === "Summarized"
                    ? "bg-blue-100 text-blue-800"
                    : "bg-gray-100 text-gray-800"
                }`}>
                  {doc.status}
                </span>
              </div>
              <Link to={`/editor?id=${doc.id}`}>
                <Button className="w-full">Open Document</Button>
              </Link>
            </div>
          ))}
        </div>
      ) : (
        <div className="text-center py-10 glass-panel">
          <h3 className="text-lg font-medium mb-2">No documents found</h3>
          <p className="text-muted-foreground mb-4">
            You haven't uploaded any documents yet.
          </p>
          <Link to="/">
            <Button>Upload a document</Button>
          </Link>
        </div>
      )}
    </div>
  );
};

export default DocumentArchive;
