import { useState, useEffect } from 'react';
import { useSearchParams, useNavigate, useLocation } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { ArrowLeft } from 'lucide-react';
import DocumentEditor from '@/components/DocumentEditor';
import Navbar from '@/components/Navbar';
import { Document } from '@/types/document';

type DocumentState = {
  document: Document; 
};

const Editor = () => {
  const [searchParams] = useSearchParams();
  const documentId = searchParams.get('id');
  const navigate = useNavigate();
  const location = useLocation();
  const state = location.state as DocumentState | undefined;
  const document = state?.document;



  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />

      <main className="flex-1 pt-24 pb-12 px-6">
        <div className="max-w-7xl mx-auto">
          <div className="mb-6">
            <Button
              variant="ghost"
              onClick={() => navigate(-1)}
              className="mb-4"
            >
              <ArrowLeft className="mr-2 h-4 w-4" />
              Back
            </Button>
            <h1 className="text-3xl font-bold">Document Editor</h1>
            <p className="text-muted-foreground">
              Edit and anonymize your document
            </p>
          </div>

          <DocumentEditor document={document} />
        </div>
      </main>
    </div>
  );
};

export default Editor;
