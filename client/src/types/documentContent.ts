export type SensitiveItem = {
  id: string;
  text: string;
  replacement: string;
};

export type DocumentContent = {
  title: string;
  paragraph: string;               
  sensitive: SensitiveItem[];     
  summary: string;
};