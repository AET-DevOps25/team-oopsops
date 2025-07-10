export type SensitiveItem = {
  id: string;
  text: string;
  replacement: string;
};

export type DocumentContent = {
  title: string;
  content: {
    paragraph: string;
    sensitive: SensitiveItem[];
  }[];
  summary: string;
};