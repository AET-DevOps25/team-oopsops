import React from 'react';
import { Button } from '@/components/ui/button';

type DocumentStatus = 'All' | 'Anonymized' | 'Original';

interface DocumentStatusFilterProps {
  selectedStatus: DocumentStatus;
  onStatusChange: (status: DocumentStatus) => void;
}

const DocumentStatusFilter = ({
  selectedStatus,
  onStatusChange,
}: DocumentStatusFilterProps) => {
  const statuses: DocumentStatus[] = ['All', 'Original', 'Anonymized'];

  return (
    <div className="flex flex-wrap gap-2 mb-6">
      <span className="text-sm font-medium text-muted-foreground mr-2 flex items-center">
        Filter by status:
      </span>
      {statuses.map((status) => (
        <Button
          key={status}
          variant={selectedStatus === status ? 'default' : 'outline'}
          size="sm"
          onClick={() => onStatusChange(status)}
          className="transition-all duration-200"
        >
          {status}
        </Button>
      ))}
    </div>
  );
};

export default DocumentStatusFilter;
