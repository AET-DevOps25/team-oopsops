export type User = {
  username: string;
  email: string;
};

export type RegistrationData = {
  username: string;
  email: string;
  password: string;
};

export type DecodedToken = {
  preferred_username: string;
  email: string;
  exp: number;
};