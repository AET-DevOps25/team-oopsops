import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import DocumentStatusFilter from '@/components/DocumentStatusFilter';

// Mock data for demonstration
const mockDocuments = [
  {
    id: 'doc1',
    name: 'Financial Report 2024',
    date: '2024-05-10',
    status: 'Anonymized',
  },
  {
    id: 'doc2',
    name: 'Employee Records',
    date: '2024-05-08',
    status: 'Original',
  },
  {
    id: 'doc3',
    name: 'Contract Agreement',
    date: '2024-05-05',
    status: 'Summarized',
  },
];

type DocumentStatus = 'All' | 'Anonymized' | 'Original' | 'Summarized';

const DocumentArchive = () => {
  const [selectedStatus, setSelectedStatus] = useState<DocumentStatus>('All');

  const filteredDocuments =
    selectedStatus === 'All'
      ? mockDocuments
      : mockDocuments.filter((doc) => doc.status === selectedStatus);

  return (
    <div className="space-y-6">
      <DocumentStatusFilter
        selectedStatus={selectedStatus}
        onStatusChange={setSelectedStatus}
      />

      {filteredDocuments.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredDocuments.map((doc) => (
            <div
              key={doc.id}
              className="glass-panel p-6 rounded-xl transition-all duration-300 hover:scale-105 hover:shadow-lg hover:shadow-primary/10 hover:border-primary/20 cursor-pointer group"
            >
              <h3 className="font-medium text-lg mb-2 group-hover:text-primary transition-colors duration-200">
                {doc.name}
              </h3>
              <div className="flex justify-between text-sm text-muted-foreground mb-4">
                <span>{doc.date}</span>
                <span
                  className={`px-2 py-0.5 rounded-full transition-all duration-200 ${
                    doc.status === 'Anonymized'
                      ? 'bg-green-100 text-green-800 group-hover:bg-green-200'
                      : doc.status === 'Summarized'
                      ? 'bg-blue-100 text-blue-800 group-hover:bg-blue-200'
                      : 'bg-gray-100 text-gray-800 group-hover:bg-gray-200'
                  }`}
                >
                  {doc.status}
                </span>
              </div>
              <Link to={`/editor?id=${doc.id}`}>
                <Button className="w-full transition-all duration-200 group-hover:bg-primary/90">
                  Open Document
                </Button>
              </Link>
            </div>
          ))}
        </div>
      ) : (
        <div className="text-center py-10 glass-panel">
          <h3 className="text-lg font-medium mb-2">No documents found</h3>
          <p className="text-muted-foreground mb-4">
            {selectedStatus === 'All'
              ? "You haven't uploaded any documents yet."
              : `No ${selectedStatus.toLowerCase()} documents found.`}
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
