import React, { useState, useRef } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
  MessageCircle,
  Minimize2,
  Maximize2,
  X,
  Send,
  Loader2,
  Trash2,
} from 'lucide-react';
import { useChat } from '@/hooks/useChat';
import { ChatMessage } from '@/types/chat';

interface ChatWidgetProps {
  documentContent?: string;
  documentTitle?: string;
  documentStatus?: 'original' | 'anonymized';
  isVisible: boolean;
}

const ChatWidget: React.FC<ChatWidgetProps> = ({
  documentContent,
  documentTitle,
  documentStatus = 'original',
  isVisible,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [isMinimized, setIsMinimized] = useState(false);
  const [inputValue, setInputValue] = useState('');
  const inputRef = useRef<HTMLInputElement>(null);

  const {
    messages,
    isLoading,
    sendMessage,
    clearChat,
    messagesEndRef,
    isDocumentUploaded,
    uploadDocument,
  } = useChat(documentContent, documentTitle, documentStatus);

  const handleSendMessage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputValue.trim() || isLoading) return;

    await sendMessage(inputValue);
    setInputValue('');
    inputRef.current?.focus();
  };

  const handleToggle = () => {
    if (!isOpen) {
      setIsOpen(true);
      setIsMinimized(false);
      // Upload document when chat is first opened
      if (!isDocumentUploaded && documentContent) {
        uploadDocument();
      }
    } else {
      setIsOpen(false);
    }
  };

  const formatTimestamp = (timestamp?: Date) => {
    if (!timestamp) return '';
    return new Intl.DateTimeFormat('en-US', {
      hour: '2-digit',
      minute: '2-digit',
    }).format(timestamp);
  };

  const renderMessage = (message: ChatMessage, index: number) => (
    <div
      key={index}
      className={`flex ${
        message.role === 'user' ? 'justify-end' : 'justify-start'
      } mb-3`}
    >
      <div
        className={`max-w-[80%] rounded-lg px-3 py-2 ${
          message.role === 'user'
            ? 'bg-primary text-primary-foreground'
            : 'bg-muted'
        }`}
      >
        <div className="text-sm">{message.content}</div>
        {message.timestamp && (
          <div
            className={`text-xs mt-1 ${
              message.role === 'user'
                ? 'text-primary-foreground/70'
                : 'text-muted-foreground'
            }`}
          >
            {formatTimestamp(message.timestamp)}
          </div>
        )}
      </div>
    </div>
  );

  if (!isVisible) {
    return null;
  }

  return (
    <div className="fixed bottom-4 right-4 z-50">
      {/* Chat Toggle Button */}
      {!isOpen && (
        <Button
          onClick={handleToggle}
          className="rounded-full w-14 h-14 shadow-lg hover:shadow-xl transition-shadow"
          size="icon"
        >
          <MessageCircle className="h-6 w-6" />
          <span className="sr-only">Open chat</span>
        </Button>
      )}

      {/* Chat Window */}
      {isOpen && (
        <Card
          className={`transition-all duration-300 ease-in-out shadow-xl ${
            isMinimized ? 'w-80 h-14' : 'w-96 h-[500px] sm:w-80 sm:h-[450px]'
          }`}
        >
          <CardHeader className="flex flex-row items-center justify-between p-3 bg-primary text-primary-foreground">
            <CardTitle className="text-sm font-medium">
              Chat with Document
              {!isDocumentUploaded && documentContent && (
                <span className="text-xs block text-primary-foreground/70">
                  Setting up document...
                </span>
              )}
              {isDocumentUploaded && (
                <span className="text-xs block text-primary-foreground/70">
                  {documentStatus === 'anonymized'
                    ? '🔒 Anonymized'
                    : '📄 Original'}{' '}
                  content
                </span>
              )}
            </CardTitle>
            <div className="flex items-center space-x-1">
              <Button
                variant="ghost"
                size="icon"
                onClick={clearChat}
                className="h-6 w-6 text-primary-foreground hover:bg-primary-foreground/20"
              >
                <Trash2 className="h-3 w-3" />
                <span className="sr-only">Clear chat</span>
              </Button>
              <Button
                variant="ghost"
                size="icon"
                onClick={() => setIsMinimized(!isMinimized)}
                className="h-6 w-6 text-primary-foreground hover:bg-primary-foreground/20"
              >
                {isMinimized ? (
                  <Maximize2 className="h-3 w-3" />
                ) : (
                  <Minimize2 className="h-3 w-3" />
                )}
                <span className="sr-only">
                  {isMinimized ? 'Maximize' : 'Minimize'}
                </span>
              </Button>
              <Button
                variant="ghost"
                size="icon"
                onClick={() => setIsOpen(false)}
                className="h-6 w-6 text-primary-foreground hover:bg-primary-foreground/20"
              >
                <X className="h-3 w-3" />
                <span className="sr-only">Close chat</span>
              </Button>
            </div>
          </CardHeader>

          {!isMinimized && (
            <CardContent className="flex flex-col h-[calc(100%-3.5rem)] p-0">
              {/* Messages Area */}
              <ScrollArea className="flex-1 p-4">
                {messages.length === 0 ? (
                  <div className="text-center text-muted-foreground py-8">
                    <MessageCircle className="h-8 w-8 mx-auto mb-2 opacity-50" />
                    <p className="text-sm">
                      Start chatting about your document!
                    </p>
                    {documentContent && (
                      <p className="text-xs mt-1">
                        Document: {documentTitle || 'Untitled'} (
                        {documentStatus})
                      </p>
                    )}
                  </div>
                ) : (
                  <div>
                    {messages.map((message, index) =>
                      renderMessage(message, index)
                    )}
                    {isLoading && (
                      <div className="flex justify-start mb-3">
                        <div className="bg-muted rounded-lg px-3 py-2 flex items-center">
                          <Loader2 className="h-4 w-4 animate-spin mr-2" />
                          <span className="text-sm text-muted-foreground">
                            Thinking...
                          </span>
                        </div>
                      </div>
                    )}
                    <div ref={messagesEndRef} />
                  </div>
                )}
              </ScrollArea>

              {/* Input Area */}
              <div className="border-t p-3">
                <form onSubmit={handleSendMessage} className="flex space-x-2">
                  <Input
                    ref={inputRef}
                    value={inputValue}
                    onChange={(e) => setInputValue(e.target.value)}
                    placeholder={
                      isDocumentUploaded
                        ? 'Ask about your document...'
                        : 'Setting up document...'
                    }
                    disabled={isLoading || !isDocumentUploaded}
                    className="flex-1 text-sm"
                  />
                  <Button
                    type="submit"
                    size="icon"
                    disabled={
                      isLoading || !inputValue.trim() || !isDocumentUploaded
                    }
                    className="shrink-0"
                  >
                    {isLoading ? (
                      <Loader2 className="h-4 w-4 animate-spin" />
                    ) : (
                      <Send className="h-4 w-4" />
                    )}
                    <span className="sr-only">Send message</span>
                  </Button>
                </form>
              </div>
            </CardContent>
          )}
        </Card>
      )}
    </div>
  );
};

export default ChatWidget;
