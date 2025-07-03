import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { ArrowLeft } from 'lucide-react';
import DocumentEditor from '@/components/DocumentEditor';

const Editor = () => {
  const [searchParams] = useSearchParams();
  const documentId = searchParams.get('id');
  const navigate = useNavigate();

  return (
    <div className="min-h-screen flex flex-col">
      <header className="glass-panel h-16 fixed top-0 left-0 right-0 z-50">
        <div className="max-w-7xl mx-auto h-full flex items-center px-6">
          <div className="font-bold text-xl">Redacta</div>
        </div>
      </header>

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

          <DocumentEditor documentId={documentId} />
        </div>
      </main>
    </div>
  );
};

export default Editor;
