import React from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Check, X } from 'lucide-react';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';

type EditItem = {
  id: string;
  text: string;
  replacement: string;
};

type EditAnonymizedDialogProps = {
  isOpen: boolean;
  onClose: () => void;
  currentEditItem: EditItem | null;
  tempReplacement: string;
  onReplacementChange: (value: string) => void;
  onSave: () => void;
  onCancel: () => void;
};

const EditAnonymizedDialog = ({
  isOpen,
  onClose,
  currentEditItem,
  tempReplacement,
  onReplacementChange,
  onSave,
  onCancel,
}: EditAnonymizedDialogProps) => {
  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Edit Anonymized Text</DialogTitle>
          <DialogDescription>
            Customize how this text will appear in the anonymized document.
          </DialogDescription>
        </DialogHeader>
        {currentEditItem && (
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <p className="text-sm font-medium">Original Text:</p>
              <div className="text-sensitive p-2 bg-muted/50 rounded">
                {currentEditItem.text}
              </div>
            </div>
            <div className="space-y-2">
              <p className="text-sm font-medium">Replacement Text:</p>
              <Input
                value={tempReplacement}
                onChange={(e) => onReplacementChange(e.target.value)}
                placeholder="Enter replacement text"
                className="text-anonymized"
              />
            </div>
          </div>
        )}
        <DialogFooter className="flex space-x-2 sm:justify-end">
          <Button type="button" variant="outline" onClick={onCancel}>
            <X className="mr-2 h-4 w-4" />
            Cancel
          </Button>
          <Button type="button" onClick={onSave}>
            <Check className="mr-2 h-4 w-4" />
            Save Changes
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default EditAnonymizedDialog;
